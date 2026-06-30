package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.DeliveryOrder;
import com.baidu.myshop.pojo.OrderDetail;
import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.pojo.OutboundOrder;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.service.DeliveryOrderService;
import com.baidu.myshop.service.InventoryService;
import com.baidu.myshop.service.OrderDetailService;
import com.baidu.myshop.service.OutboundOrderService;
import com.baidu.myshop.service.OrdersService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/outbound")
public class OutboundOrderController {

    @Autowired
    private OutboundOrderService outboundOrderService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private com.baidu.myshop.service.UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    @RequestMapping("/toList")
    public String toList() {
        return "outbound/list";
    }

    @RequestMapping("/toAdd")
    public String toAdd(Model model) {
        List<Orders> orders = ordersService.list();
        List<Product> products = productService.list();
        model.addAttribute("orders", orders);
        model.addAttribute("products", products);
        return "outbound/add";
    }

    @RequestMapping("/toDetail/{id}")
    public String toDetail(@PathVariable Integer id, Model model) {
        OutboundOrder outboundOrder = outboundOrderService.getById(id);
        model.addAttribute("outbound", outboundOrder);
        
        if (outboundOrder.getOperator() != null && !outboundOrder.getOperator().isEmpty()) {
            try {
                var operator = userService.getById(Integer.valueOf(outboundOrder.getOperator()));
                model.addAttribute("operatorName", operator != null ? operator.getName() : "未知");
            } catch (NumberFormatException e) {
                model.addAttribute("operatorName", outboundOrder.getOperator());
            }
        } else {
            model.addAttribute("operatorName", "未知");
        }
        
        if (outboundOrder.getProductId() != null) {
            var product = productService.getById(outboundOrder.getProductId());
            model.addAttribute("productName", product != null ? product.getPname() : "未知");
        } else {
            model.addAttribute("productName", "未知");
        }
        
        return "outbound/detail";
    }

    @PostMapping("/list")
    @ResponseBody
    public PageResult<OutboundOrder> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            Integer type,
            String orderNo) {
        return outboundOrderService.getPage(page, limit, type, orderNo);
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody OutboundOrder outboundOrder, HttpSession session) {
        try {
            com.baidu.myshop.pojo.User user2 = (com.baidu.myshop.pojo.User) session.getAttribute("user2");
            if (user2 != null && user2.getUid() != null) {
                outboundOrder.setOperator(String.valueOf(user2.getUid()));
                System.out.println("操作人ID: " + user2.getUid() + ", 操作人名称: " + user2.getName());
            } else {
                System.out.println("警告：未找到登录用户，操作人将为空");
            }
            
            boolean success = outboundOrderService.createOutboundOrder(outboundOrder);
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
            boolean success = outboundOrderService.approveOutbound(id);
            if (success) {
                return AjaxResult.right("审核通过，库存已扣减");
            }
            return AjaxResult.error("审核失败，库存不足");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer id) {
        try {
            boolean success = outboundOrderService.removeById(id);
            if (success) {
                return AjaxResult.right("删除成功");
            }
            return AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @GetMapping("/getOrders")
    @ResponseBody
    public List<Orders> getOrders() {
        return ordersService.list();
    }

    @GetMapping("/getProducts")
    @ResponseBody
    public List<Product> getProducts() {
        return productService.list();
    }

    @GetMapping("/getStock/{productId}")
    @ResponseBody
    public Map<String, Object> getStock(@PathVariable Integer productId) {
        Map<String, Object> result = new HashMap<>();
        var inventory = inventoryService.getByProductId(productId);
        if (inventory != null) {
            result.put("quantity", inventory.getQuantity());
        } else {
            result.put("quantity", 0);
        }
        return result;
    }

    @GetMapping("/getPendingOrders")
    @ResponseBody
    public List<Orders> getPendingOrders() {
        List<DeliveryOrder> deliveryOrders = deliveryOrderService.list();
        List<OutboundOrder> outboundOrders = outboundOrderService.list();
        
        List<Integer> deliveryOrderIds = deliveryOrders.stream()
                .filter(d -> d.getOrderId() != null)
                .map(DeliveryOrder::getOrderId)
                .toList();
        
        List<Integer> outboundOrderIds = outboundOrders.stream()
                .filter(o -> o.getOrderId() != null)
                .map(OutboundOrder::getOrderId)
                .toList();
        
        List<Integer> excludedIds = new java.util.ArrayList<>();
        excludedIds.addAll(deliveryOrderIds);
        excludedIds.addAll(outboundOrderIds);
        
        if (excludedIds.isEmpty()) {
            return ordersService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Orders>()
                            .like("orderNo", "XS")
            );
        }
        
        return ordersService.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Orders>()
                        .like("orderNo", "XS")
                        .notIn("oid", excludedIds)
        );
    }
    
    @GetMapping("/getOrderDetails/{orderId}")
    @ResponseBody
    public Map<String, Object> getOrderDetails(@PathVariable Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        
        List<OrderDetail> details = orderDetailService.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderDetail>()
                        .eq("oid", orderId));
        
        for (OrderDetail detail : details) {
            var product = productService.getById(detail.getPid());
            if (product != null) {
                detail.setPname(product.getPname());
            }
        }
        result.put("details", details);
        return result;
    }

    @GetMapping("/getOrderByDelivery/{deliveryId}")
    @ResponseBody
    public Map<String, Object> getOrderByDelivery(@PathVariable Integer deliveryId) {
        Map<String, Object> result = new HashMap<>();
        DeliveryOrder delivery = deliveryOrderService.getById(deliveryId);
        if (delivery != null && delivery.getOrderId() != null) {
            Orders order = ordersService.getOrderByOid(delivery.getOrderId());
            result.put("order", order);
            
            List<OrderDetail> details = orderDetailService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderDetail>()
                            .eq("oid", delivery.getOrderId()));
            
            for (OrderDetail detail : details) {
                var product = productService.getById(detail.getPid());
                if (product != null) {
                    detail.setPname(product.getPname());
                }
            }
            result.put("details", details);
        }
        return result;
    }
}