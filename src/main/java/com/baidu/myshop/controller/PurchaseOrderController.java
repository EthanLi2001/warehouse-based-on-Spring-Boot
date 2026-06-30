package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.PurchaseOrder;
import com.baidu.myshop.pojo.PurchasePlan;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.pojo.Supplier;
import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.service.PurchaseOrderService;
import com.baidu.myshop.service.PurchasePlanService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.service.SupplierService;
import com.baidu.myshop.service.InventoryService;
import com.baidu.myshop.utils.AjaxResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchasePlanService purchasePlanService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/toList")
    public String toList() {
        return "purchase/order/list";
    }

    @GetMapping("/toDetail/{poId}")
    public String toDetail(@PathVariable Integer poId, Model model) {
        PurchaseOrder order = purchaseOrderService.getById(poId);
        model.addAttribute("order", order);
        return "purchase/order/detail";
    }

    @GetMapping("/getPage")
    @ResponseBody
    public Map<String, Object> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer status) {

        Page<PurchaseOrder> pageParam = new Page<>(page, limit);
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();

        if (status != null) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("createTime");
        IPage<PurchaseOrder> pageResult = purchaseOrderService.page(pageParam, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", pageResult.getTotal());
        result.put("data", pageResult.getRecords());
        return result;
    }

    @PostMapping("/createFromPlan/{planId}")
    @ResponseBody
    public AjaxResult createFromPlan(@PathVariable Integer planId) {
        PurchasePlan plan = purchasePlanService.getById(planId);
        if (plan == null) {
            return AjaxResult.error("采购计划不存在");
        }

        if (plan.getStatus() != 0 && plan.getStatus() != 1) {
            return AjaxResult.error("该计划状态不允许生成订单");
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setPoNo(purchaseOrderService.generatePoNo());
        order.setPlanId(planId);
        order.setProductId(plan.getProductId());
        order.setQuantity(plan.getQuantity());
        order.setStatus(0);
        order.setCreateTime(new Date());

        Product product = productService.getById(plan.getProductId());
        if (product != null) {
            order.setProductName(product.getPname());
            order.setPrice(product.getPrice());
        }

        if (plan.getSupplierName() != null && !plan.getSupplierName().isEmpty()) {
            order.setSupplierName(plan.getSupplierName());
            // 根据供应商名称查找 supplierId
            var supplier = supplierService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.baidu.myshop.pojo.Supplier>()
                    .eq("supplierName", plan.getSupplierName()));
            if (supplier != null) {
                order.setSupplierId(supplier.getSupplierId());
            }
        }

        boolean success = purchaseOrderService.save(order);
        if (success) {
            plan.setStatus(2);
            plan.setUpdateTime(new Date());
            purchasePlanService.updateById(plan);
            return AjaxResult.right("订单创建成功");
        }
        return AjaxResult.error("订单创建失败");
    }

    @PostMapping("/updateStatus")
    @ResponseBody
    public AjaxResult updateStatus(@RequestParam Integer poId, @RequestParam Integer status) {
        PurchaseOrder order = purchaseOrderService.getById(poId);
        if (order == null) {
            return AjaxResult.error("订单不存在");
        }

        Integer currentStatus = order.getStatus();
        String statusChangeLog = "[" + new Date().toString() + "] 状态变更: " + currentStatus + " -> " + status;

        if (order.getRemark() != null) {
            order.setRemark(order.getRemark() + "\n" + statusChangeLog);
        } else {
            order.setRemark(statusChangeLog);
        }

        order.setStatus(status);
        order.setCreateTime(new Date());

        boolean success = purchaseOrderService.updateById(order);
        if (success) {
            if (status == 3) {
                Inventory inventory = inventoryService.getByProductId(order.getProductId());
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity() + order.getQuantity());
                    inventoryService.updateById(inventory);
                } else {
                    Inventory newInventory = new Inventory();
                    newInventory.setProductId(order.getProductId());
                    newInventory.setQuantity(order.getQuantity());
                    newInventory.setMinStock(0);
                    newInventory.setMaxStock(1000);
                    newInventory.setCreateTime(new Date());
                    newInventory.setUpdateTime(new Date());
                    inventoryService.save(newInventory);
                }
            }
            return AjaxResult.right("状态更新成功");
        }
        return AjaxResult.error("状态更新失败");
    }

    @PostMapping("/delete/{poId}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer poId) {
        boolean success = purchaseOrderService.removeById(poId);
        if (success) {
            return AjaxResult.right("删除成功");
        }
        return AjaxResult.error("删除失败");
    }
}