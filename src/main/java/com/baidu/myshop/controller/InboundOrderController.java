package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.InboundOrder;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.pojo.Supplier;
import com.baidu.myshop.service.InboundOrderService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.service.SupplierService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inbound")
public class InboundOrderController {

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private com.baidu.myshop.service.UserService userService;

    @RequestMapping("/toList")
    public String toList() {
        return "inbound/list";
    }

    @RequestMapping("/toAdd")
    public String toAdd(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<Product> products = productService.list();
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("products", products);
        return "inbound/add";
    }

    @RequestMapping("/toDetail/{id}")
    public String toDetail(@PathVariable Integer id, Model model) {
        InboundOrder inboundOrder = inboundOrderService.getById(id);
        model.addAttribute("inbound", inboundOrder);
        
        if (inboundOrder.getOperator() != null && !inboundOrder.getOperator().isEmpty()) {
            try {
                var operator = userService.getById(Integer.valueOf(inboundOrder.getOperator()));
                model.addAttribute("operatorName", operator != null ? operator.getName() : "未知");
            } catch (NumberFormatException e) {
                model.addAttribute("operatorName", inboundOrder.getOperator());
            }
        } else {
            model.addAttribute("operatorName", "未知");
        }
        
        if (inboundOrder.getProductId() != null) {
            var product = productService.getById(inboundOrder.getProductId());
            model.addAttribute("productName", product != null ? product.getPname() : "未知");
        } else {
            model.addAttribute("productName", "未知");
        }
        
        if (inboundOrder.getSupplierId() != null) {
            var supplier = supplierService.getById(inboundOrder.getSupplierId());
            model.addAttribute("supplierName", supplier != null ? supplier.getSupplierName() : "未知");
        } else {
            model.addAttribute("supplierName", "未知");
        }
        
        return "inbound/detail";
    }

    @PostMapping("/list")
    @ResponseBody
    public PageResult<InboundOrder> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            Integer type,
            String orderNo) {
        return inboundOrderService.getPage(page, limit, type, orderNo);
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody InboundOrder inboundOrder, HttpSession session) {
        try {
            com.baidu.myshop.pojo.User user2 = (com.baidu.myshop.pojo.User) session.getAttribute("user2");
            if (user2 != null && user2.getUid() != null) {
                inboundOrder.setOperator(String.valueOf(user2.getUid()));
            }
            boolean success = inboundOrderService.createInboundOrder(inboundOrder);
            if (success) {
                return AjaxResult.right("创建成功");
            }
            return AjaxResult.error("创建失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/approve/{id}")
    @ResponseBody
    public AjaxResult approve(@PathVariable Integer id) {
        try {
            boolean success = inboundOrderService.approveInbound(id);
            if (success) {
                return AjaxResult.right("审核通过，库存已更新");
            }
            return AjaxResult.error("审核失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer id) {
        try {
            boolean success = inboundOrderService.removeById(id);
            if (success) {
                return AjaxResult.right("删除成功");
            }
            return AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @GetMapping("/getSuppliers")
    @ResponseBody
    public List<Supplier> getSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/getProducts")
    @ResponseBody
    public List<Product> getProducts() {
        return productService.list();
    }

    @RequestMapping("/toBatchRecords")
    public String toBatchRecords() {
        return "inbound/batch_records";
    }

    @GetMapping("/batchRecords")
    @ResponseBody
    public Map<String, Object> getBatchRecords() {
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<InboundOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.isNotNull("batchNo");
        wrapper.ne("batchNo", "");
        wrapper.orderByDesc("createTime");
        List<InboundOrder> inboundOrders = inboundOrderService.list(wrapper);
        
        LinkedHashMap<String, List<Map<String, Object>>> batchGroups = new LinkedHashMap<>();
        for (InboundOrder order : inboundOrders) {
            String batchNo = order.getBatchNo();
            batchGroups.putIfAbsent(batchNo, new ArrayList<>());
            
            Map<String, Object> item = new HashMap<>();
            item.put("inboundId", order.getInboundId());
            item.put("orderNo", order.getOrderNo());
            item.put("quantity", order.getQuantity());
            item.put("createTime", order.getCreateTime());
            
            if (order.getProductId() != null) {
                var product = productService.getById(order.getProductId());
                item.put("productName", product != null ? product.getPname() : "未知商品");
            } else {
                item.put("productName", "未知商品");
            }
            
            if (order.getSupplierId() != null) {
                var supplier = supplierService.getById(order.getSupplierId());
                item.put("supplierName", supplier != null ? supplier.getSupplierName() : "未知供应商");
            } else {
                item.put("supplierName", "未知供应商");
            }
            
            batchGroups.get(batchNo).add(item);
        }
        
        result.put("code", 0);
        result.put("data", batchGroups);
        result.put("count", batchGroups.size());
        return result;
    }
}