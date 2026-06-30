package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.DeliveryOrder;
import com.baidu.myshop.pojo.OrderDetail;
import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.service.DeliveryOrderService;
import com.baidu.myshop.service.OrderDetailService;
import com.baidu.myshop.service.OrdersService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.service.CustomerService;
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
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CustomerService customerService;

    @RequestMapping("/toList")
    public String toList() {
        return "delivery/list";
    }

    @RequestMapping("/toAdd")
    public String toAdd(Model model, @RequestParam(value = "orderId", required = false) Integer orderId) {
        Orders selectedOrder = null;
        
        if (orderId != null) {
            selectedOrder = ordersService.getOrderByOid(orderId);
            if (selectedOrder != null && selectedOrder.getCustomerId() != null) {
                var customer = customerService.getById(selectedOrder.getCustomerId());
                if (customer != null) {
                    selectedOrder.setUserName(customer.getCustomerName());
                }
            }
        }
        
        model.addAttribute("selectedOrder", selectedOrder);
        return "delivery/add";
    }

    @PostMapping("/list")
    @ResponseBody
    public PageResult<DeliveryOrder> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            String orderNo,
            Integer status) {
        return deliveryOrderService.getPage(page, limit, orderNo, status);
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody DeliveryOrder deliveryOrder, HttpSession session) {
        try {
            com.baidu.myshop.pojo.User user2 = (com.baidu.myshop.pojo.User) session.getAttribute("user2");
            if (user2 != null && user2.getUid() != null) {
                deliveryOrder.setOperator(String.valueOf(user2.getUid()));
            }
            boolean success = deliveryOrderService.createDeliveryOrder(deliveryOrder);
            if (success) {
                return AjaxResult.right("发货成功，库存已扣减");
            }
            return AjaxResult.error("发货失败");
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/confirm/{id}")
    @ResponseBody
    public AjaxResult confirm(@PathVariable Integer id) {
        try {
            boolean success = deliveryOrderService.confirmDelivery(id);
            if (success) {
                return AjaxResult.right("发货成功，库存已扣减");
            }
            return AjaxResult.error("发货失败，库存不足");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer id) {
        try {
            boolean success = deliveryOrderService.removeById(id);
            if (success) {
                return AjaxResult.right("删除成功");
            }
            return AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @GetMapping("/getOrderDetails/{orderId}")
    @ResponseBody
    public Map<String, Object> getOrderDetails(@PathVariable Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        Orders order = ordersService.getOrderByOid(orderId);
        if (order != null) {
            result.put("order", order);
            List<OrderDetail> details = orderDetailService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderDetail>().eq("oid", orderId));
            
            for (OrderDetail detail : details) {
                var product = productService.getById(detail.getPid());
                if (product != null) {
                    detail.setPrice(product.getPrice());
                    detail.setPname(product.getPname());
                }
            }
            result.put("details", details);
        }
        return result;
    }

    @GetMapping("/getPendingOrders")
    @ResponseBody
    public List<Orders> getPendingOrders() {
        return ordersService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Orders>()
                .eq("status", 2));
    }
}
