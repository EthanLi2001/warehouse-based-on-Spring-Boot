package com.baidu.myshop.controller;


import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.pojo.Role;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.pojo.request.ProductEditRequest;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaoye
 * @since 2025-04-15

 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private com.baidu.myshop.service.InventoryService inventoryService;

    @RequestMapping("/toEdit/{pid}")
    public String toEditProduct(@PathVariable Integer pid, Model model) {
        Product product = productService.getById(pid);
        model.addAttribute("editProd", product);
        
        Map<String, Object> inventory = getInventory(pid);
        model.addAttribute("inventory", inventory);
        
        return "prod/edit";
    }

    @RequestMapping("/toDetail/{pid}")
    public String toDetailProduct(@PathVariable Integer pid, Model model) {
        Product product = productService.getById(pid);
        model.addAttribute("editProd", product);
        
        Map<String, Object> inventory = getInventory(pid);
        model.addAttribute("inventory", inventory);
        
        return "prod/detail";
    }

    @PostMapping("/doEdit")
    @ResponseBody
    public AjaxResult editProduct(@RequestBody ProductEditRequest request) {
        System.out.println("=== ProductController: doEdit 方法被调用 ===");
        System.out.println("pid: " + request.getPid());
        System.out.println("minStock: " + request.getMinStock());
        System.out.println("maxStock: " + request.getMaxStock());
        
        Product product = new Product();
        product.setPid(request.getPid());
        product.setPname(request.getPname());
        product.setPrice(request.getPrice());
        product.setPfile(request.getPfile());
        product.setDescription(request.getDescription());
        product.setMinStock(request.getMinStock());
        product.setMaxStock(request.getMaxStock());
        
        boolean flag = productService.updateById(product);
        System.out.println("商品表更新结果: " + flag);
        
        if (request.getMinStock() != null || request.getMaxStock() != null) {
            int actualMin = request.getMinStock() != null ? request.getMinStock() : 0;
            int actualMax = request.getMaxStock() != null ? request.getMaxStock() : 0;
            boolean inventoryUpdated = inventoryService.updateMinMaxStock(request.getPid(), actualMin, actualMax);
            System.out.println("库存表更新结果: " + inventoryUpdated);
        }
        
        if (flag){
            return new AjaxResult();
        }
        return AjaxResult.error();
    }

    @PostMapping("/toAdd")
    public String toAdd(){
        return "prod/add";
    }


    @PostMapping("/doAdd")
    @ResponseBody
    public AjaxResult addProduct(Product product, Integer minStock, Integer maxStock){
        boolean flag = productService.save(product);
        
        if (flag) {
            Inventory inventory = new Inventory();
            inventory.setProductId(product.getPid());
            inventory.setQuantity(0);
            inventory.setMinStock(minStock != null ? minStock : 0);
            inventory.setMaxStock(maxStock != null ? maxStock : 1000);
            java.util.Date now = new java.util.Date();
            inventory.setCreateTime(now);
            inventory.setUpdateTime(now);
            inventoryService.save(inventory);
        }
        
        if (!flag){
            return AjaxResult.error();
        }
        return AjaxResult.right();
    }
    @PostMapping("/batchDel")
    @ResponseBody
    public AjaxResult batchDeleteUsers(@RequestBody Map<String, List<Integer>> params) {
        List<Integer> pid = params.get("pid");
        if (pid == null || pid.isEmpty()) {
            return AjaxResult.error("请选择要删除的数据");
        }
        boolean success = productService.removeByIds(pid);
        if (success) {
            return new AjaxResult();
        } else {
            return AjaxResult.error("批量删除失败");
        }
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("code", 1);
            response.put("msg", "上传失败，因为文件为空");
            return response;
        }

        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            
            // 使用原始文件名
            // 注意：如果原始文件名包含特殊字符（如中文、空格等），
            // 即使配置了UTF-8编码，也可能因服务器环境或URL解析问题导致后续访问404。
            // 建议使用仅含英文字母、数字、下划线、短横线的文件名以保证兼容性。
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                response.put("code", 1);
                response.put("msg", "上传失败，文件名无效。");
                return response;
            }
            
            Path filePath = uploadPath.resolve(originalFilename);
            System.out.println("Attempting to save file to: " + filePath.toAbsolutePath().toString());

            // 写入文件
            file.transferTo(filePath);
            response.put("code", 0);
            response.put("msg", "上传成功");
            response.put("data", originalFilename); // 返回原始文件名

        } catch (IOException e) {
            response.put("code", 1);
            response.put("msg", "文件上传失败，请联系管理员。错误: " + e.getMessage()); 
            // logger.error("File upload failed for original name: " + file.getOriginalFilename(), e);
        }

        return response;
    }
    @RequestMapping("/toList")
    public String toList(){
        return "prod/list";
    }
    @RequestMapping("/toAdd")
    public String toAddProduct(){
        return "prod/add";
    }

    @PostMapping("/doDel/{uid}")
    @ResponseBody
    public AjaxResult doDel(@PathVariable Integer uid) {
        boolean success = productService.removeById(uid);
        if (success) {
            return new AjaxResult();
        } else {
            return AjaxResult.error("删除失败");
        }
    }
    @PostMapping ("/getPage")
    @ResponseBody
    public PageResult<Product> getPage(String pname, @RequestParam(defaultValue = "1")Integer page,
                                       @RequestParam(defaultValue = "5")  Integer limit){
        System.out.println("------getPage-------");
        System.out.println("pname="+pname);
        System.out.println("page="+page);
        System.out.println("limit="+limit);
        if ("".equals(pname)){
            pname=null;
        }
        PageResult<Product> result= productService.getProductByPage(pname,page,limit);
        return result;
    }

    @RequestMapping("/toUserList")
    public String toUserProductList(){
        return "prod/user_list";
    }

    @GetMapping("/getInventory/{pid}")
    @ResponseBody
    public Map<String, Object> getInventory(@PathVariable Integer pid) {
        Map<String, Object> result = new HashMap<>();
        var inventory = inventoryService.getByProductId(pid);
        if (inventory != null) {
            result.put("quantity", inventory.getQuantity());
            result.put("minStock", inventory.getMinStock());
            result.put("maxStock", inventory.getMaxStock());
            result.put("batchNo", inventory.getBatchNo());
            
            if (inventory.getQuantity() < inventory.getMinStock()) {
                result.put("warning", "low");
            } else if (inventory.getQuantity() > inventory.getMaxStock()) {
                result.put("warning", "high");
            } else {
                result.put("warning", "normal");
            }
        } else {
            result.put("quantity", 0);
            result.put("minStock", 0);
            result.put("maxStock", 0);
            result.put("batchNo", "");
            result.put("warning", "normal");
        }
        return result;
    }

}
