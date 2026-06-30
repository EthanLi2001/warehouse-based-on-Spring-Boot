package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.PurchasePlan;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.pojo.Supplier;
import com.baidu.myshop.service.PurchasePlanService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.service.SupplierService;
import com.baidu.myshop.utils.AjaxResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/purchase/plan")
public class PurchasePlanController {

    @Autowired
    private PurchasePlanService purchasePlanService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/list")
    public String list() {
        return "purchase/plan/list";
    }

    @GetMapping("/toAdd")
    public String toAdd(Model model) {
        List<Product> products = productService.list();
        List<Supplier> suppliers = supplierService.list();
        model.addAttribute("products", products);
        model.addAttribute("suppliers", suppliers);
        return "purchase/plan/add";
    }

    @GetMapping("/toEdit/{planId}")
    public String toEdit(@PathVariable Integer planId, Model model) {
        PurchasePlan plan = purchasePlanService.getById(planId);
        List<Product> products = productService.list();
        List<Supplier> suppliers = supplierService.list();
        model.addAttribute("plan", plan);
        model.addAttribute("products", products);
        model.addAttribute("suppliers", suppliers);
        return "purchase/plan/edit";
    }

    @GetMapping("/getPage")
    @ResponseBody
    public Map<String, Object> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String planName) {
        
        Page<PurchasePlan> pageParam = new Page<>(page, limit);
        QueryWrapper<PurchasePlan> wrapper = new QueryWrapper<>();
        
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (planName != null && !planName.isEmpty()) {
            wrapper.like("planName", planName);
        }
        
        wrapper.orderByDesc("createTime");
        IPage<PurchasePlan> pageResult = purchasePlanService.page(pageParam, wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", pageResult.getTotal());
        result.put("data", pageResult.getRecords());
        return result;
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(PurchasePlan purchasePlan, HttpSession session) {
        purchasePlan.setPlanNo(purchasePlanService.generatePlanNo());
        purchasePlan.setStatus(0);
        purchasePlan.setCreateTime(new Date());
        purchasePlan.setUpdateTime(new Date());
        
        Product product = productService.getById(purchasePlan.getProductId());
        if (product != null) {
            purchasePlan.setProductName(product.getPname());
        }
        
        boolean success = purchasePlanService.save(purchasePlan);
        if (success) {
            return AjaxResult.right("创建成功");
        }
        return AjaxResult.error("创建失败");
    }

    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(PurchasePlan purchasePlan) {
        purchasePlan.setUpdateTime(new Date());
        
        Product product = productService.getById(purchasePlan.getProductId());
        if (product != null) {
            purchasePlan.setProductName(product.getPname());
        }
        
        boolean success = purchasePlanService.updateById(purchasePlan);
        if (success) {
            return AjaxResult.right("修改成功");
        }
        return AjaxResult.error("修改失败");
    }

    @PostMapping("/delete/{planId}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer planId) {
        boolean success = purchasePlanService.removeById(planId);
        if (success) {
            return AjaxResult.right("删除成功");
        }
        return AjaxResult.error("删除失败");
    }

    @PostMapping("/batchDelete")
    @ResponseBody
    public AjaxResult batchDelete(@RequestBody List<Integer> planIds) {
        boolean success = purchasePlanService.removeByIds(planIds);
        if (success) {
            return AjaxResult.right("批量删除成功");
        }
        return AjaxResult.error("批量删除失败");
    }
}