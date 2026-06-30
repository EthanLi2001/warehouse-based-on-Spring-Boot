package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.service.InventoryService;
import com.baidu.myshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inventory/warning")
public class InventoryWarningController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @RequestMapping("/toList")
    public String toList(Model model) {
        List<Map<String, Object>> warnings = getWarningList();
        model.addAttribute("warnings", warnings);
        return "inventory/warning/list";
    }

    @RequestMapping("/toSet/{productId}")
    public String toSet(@PathVariable Integer productId, Model model) {
        Product product = productService.getById(productId);
        Inventory inventory = inventoryService.getByProductId(productId);
        
        model.addAttribute("product", product);
        if (inventory != null) {
            model.addAttribute("minStock", inventory.getMinStock());
            model.addAttribute("maxStock", inventory.getMaxStock());
            model.addAttribute("currentStock", inventory.getQuantity());
        } else {
            model.addAttribute("minStock", 0);
            model.addAttribute("maxStock", 1000);
            model.addAttribute("currentStock", 0);
        }
        return "inventory/warning/set";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer productId = Integer.parseInt(params.get("productId").toString());
            Integer minStock = Integer.parseInt(params.get("minStock").toString());
            Integer maxStock = Integer.parseInt(params.get("maxStock").toString());

            Inventory inventory = inventoryService.getByProductId(productId);
            if (inventory != null) {
                inventory.setMinStock(minStock);
                inventory.setMaxStock(maxStock);
                inventoryService.updateById(inventory);
            } else {
                inventory = new Inventory();
                inventory.setProductId(productId);
                inventory.setQuantity(0);
                inventory.setMinStock(minStock);
                inventory.setMaxStock(maxStock);
                inventoryService.save(inventory);
            }

            Product product = productService.getById(productId);
            if (product != null) {
                product.setMinStock(minStock);
                product.setMaxStock(maxStock);
                productService.updateById(product);
            }

            result.put("code", 0);
            result.put("msg", "设置成功");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "设置失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/getWarningCount")
    @ResponseBody
    public Map<String, Object> getWarningCount() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> warnings = getWarningList();
        
        int lowWarning = 0;
        int highWarning = 0;
        for (Map<String, Object> warning : warnings) {
            String type = (String) warning.get("warningType");
            if ("low".equals(type)) {
                lowWarning++;
            } else if ("high".equals(type)) {
                highWarning++;
            }
        }
        
        result.put("lowWarning", lowWarning);
        result.put("highWarning", highWarning);
        result.put("total", lowWarning + highWarning);
        return result;
    }

    @GetMapping("/getWarningList")
    @ResponseBody
    public List<Map<String, Object>> getWarningListApi() {
        return getWarningList();
    }

    @GetMapping("/getAllInventory")
    @ResponseBody
    public List<Map<String, Object>> getAllInventory() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Inventory> inventories = inventoryService.list();

        for (Inventory inv : inventories) {
            Product product = productService.getById(inv.getProductId());
            if (product == null) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("productId", inv.getProductId());
            item.put("productName", product.getPname());
            item.put("currentStock", inv.getQuantity());
            item.put("minStock", inv.getMinStock());
            item.put("maxStock", inv.getMaxStock());

            if (inv.getQuantity() < inv.getMinStock()) {
                item.put("warningType", "low");
            } else if (inv.getQuantity() > inv.getMaxStock()) {
                item.put("warningType", "high");
            } else {
                item.put("warningType", "normal");
            }

            result.add(item);
        }

        return result;
    }

    private List<Map<String, Object>> getWarningList() {
        List<Map<String, Object>> warnings = new ArrayList<>();
        List<Inventory> inventories = inventoryService.list();

        for (Inventory inv : inventories) {
            Product product = productService.getById(inv.getProductId());
            if (product == null) continue;

            Map<String, Object> warning = new HashMap<>();
            warning.put("productId", inv.getProductId());
            warning.put("productName", product.getPname());
            warning.put("currentStock", inv.getQuantity());
            warning.put("minStock", inv.getMinStock());
            warning.put("maxStock", inv.getMaxStock());

            if (inv.getQuantity() < inv.getMinStock()) {
                warning.put("warningType", "low");
                warning.put("warningMsg", "库存低于下限");
                warning.put("warningLevel", "danger");
                warnings.add(warning);
            } else if (inv.getQuantity() > inv.getMaxStock()) {
                warning.put("warningType", "high");
                warning.put("warningMsg", "库存高于上限");
                warning.put("warningLevel", "warning");
                warnings.add(warning);
            }
        }

        return warnings;
    }
}