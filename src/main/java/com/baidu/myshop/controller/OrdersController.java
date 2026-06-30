package com.baidu.myshop.controller;


import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.pojo.OrderDetail;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.pojo.Customer;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.service.OrdersService;
import com.baidu.myshop.service.OrderDetailService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.service.CustomerService;
import com.baidu.myshop.dao.InventoryMapper;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单管理表 前端控制器
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Controller
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private InventoryMapper inventoryMapper;

    /**
     * 跳转到订单列表页面
     * @return
     */
    @RequestMapping("/toList")
    public String toList(){
        return "/orders/list";
    }

    /**
     * 查询所有订单信息
     * @param oid 订单ID,用于模糊查询
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    @RequestMapping("/getPage")
    @ResponseBody
    public PageResult<Orders> getPage(
            @RequestParam(value="oid",defaultValue = "") String oid,
            @RequestParam(value = "page",defaultValue = "1") Integer currentPage,
            @RequestParam(value = "limit",defaultValue = "5") Integer pageSize){

        // 执行查询所有订单信息
        PageResult<Orders> pageResult = ordersService.getAllOrders(oid, currentPage, pageSize);
        return pageResult;
    }

    /**
     * 批量删除
     * @param oids
     * @return
     */
    @RequestMapping("/batchDel")
    @ResponseBody
    public AjaxResult batchDel(@RequestParam("oid[]") Integer[] oids) {
        int total = ordersService.deleteOrdersByIds(oids);
        if (total == oids.length) {
            // 删除成功
            return AjaxResult.right();
        } else {
            // 删除失败
            return AjaxResult.error();
        }
    }

    /**
     * 根据id删除
     * @param oid
     * @return
     */
    @RequestMapping("/doDel/{oid}")
    @ResponseBody
    public AjaxResult doDel(@PathVariable Integer oid){
        int total = ordersService.deleteOrderById(oid);
        if (total == 1){
            return AjaxResult.right();
        }else {
            return AjaxResult.error();
        }
    }

    /**
     * 跳转到订单添加页面
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(HttpServletRequest request){
        request.setAttribute("customers", customerService.getAllCustomers());
        
        List<Product> products = productService.list();
        for (Product product : products) {
            Inventory inventory = getInventoryByProductId(product.getPid());
            if (inventory != null) {
                product.setQuantity(inventory.getQuantity());
            } else {
                product.setQuantity(0);
            }
        }
        request.setAttribute("products", products);
        return "/orders/add";
    }

    /**
     * 添加订单
     * @param orders
     * @param pids 商品ID数组
     * @param quantities 数量数组
     * @return
     */
    @RequestMapping("/doAdd")
    @ResponseBody
    public AjaxResult doAdd(Orders orders, 
                            @RequestParam(value = "pids[]", required = false) Integer[] pids,
                            @RequestParam(value = "quantities[]", required = false) Integer[] quantities){
        
        if (pids == null || pids.length == 0) {
            return AjaxResult.error("请至少添加一件商品");
        }
        
        for (int i = 0; i < pids.length; i++) {
            Product product = productService.getById(pids[i]);
            if (product == null) {
                return AjaxResult.error("商品ID " + pids[i] + " 不存在");
            }
            
            Inventory inventory = getInventoryByProductId(pids[i]);
            if (inventory == null || inventory.getQuantity() < quantities[i]) {
                return AjaxResult.error("商品 '" + product.getPname() + "' 库存不足，当前库存: " + 
                    (inventory == null ? 0 : inventory.getQuantity()) + "，需求: " + quantities[i]);
            }
        }
        
        int total = ordersService.insertOrder(orders);
        if (total == 1) {
            Integer orderId = ordersService.getLastOrderId();
            for (int i = 0; i < pids.length; i++) {
                OrderDetail detail = new OrderDetail();
                detail.setOid(orderId);
                detail.setPid(pids[i]);
                detail.setQuantity(quantities[i]);
                Product product = productService.getById(pids[i]);
                if (product != null) {
                    detail.setPrice(product.getPrice());
                }
                orderDetailService.save(detail);
            }
            return AjaxResult.right();
        } else {
            return AjaxResult.error();
        }
    }
    
    private Inventory getInventoryByProductId(Integer productId) {
        try {
            return inventoryMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Inventory>()
                .eq("productId", productId));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新订单状态
     * @param oid 订单ID
     * @param status 新状态
     * @return
     */
    @RequestMapping("/updateStatus")
    @ResponseBody
    public AjaxResult updateStatus(@RequestParam Integer oid, @RequestParam Integer status) {
        Orders order = ordersService.getOrderByOid(oid);
        if (order == null) {
            return AjaxResult.error("订单不存在");
        }
        
        int currentStatus = order.getStatus();
        if (status <= currentStatus) {
            return AjaxResult.error("状态只能向后流转");
        }
        
        order.setStatus(status);
        int result = ordersService.updateOrder(order);
        if (result > 0) {
            return AjaxResult.right();
        } else {
            return AjaxResult.error("状态更新失败");
        }
    }

    /**
     * 根据id查询数据 --- 回显
     * @param oid
     * @param request
     * @return
     */
    @RequestMapping("/toEdit/{oid}")
    public String toEdit(@PathVariable("oid") Integer oid, HttpServletRequest request){
        // 根据订单主键查询指定的订单信息
        Orders orders = ordersService.getOrderByOid(oid);
        request.setAttribute("editOrder", orders);
        request.setAttribute("customers", customerService.getAllCustomers());
        return "orders/edit";
    }

    /**
     * 处理执行修改操作的请求
     * @param orders
     * @return
     */
    @RequestMapping("/doEdit")
    @ResponseBody
    public AjaxResult doEdit(Orders orders){
        int total = ordersService.updateOrder(orders);
        if (total == 1) {
            return AjaxResult.right();
        } else {
            return AjaxResult.error();
        }
    }

    @PostMapping("/createFromCart")
    @ResponseBody
    public AjaxResult createOrderFromCart(@RequestBody List<Integer> cartItemIds, HttpSession session) {
        System.out.println("====== OrdersController: createOrderFromCart 方法被调用 ======");
        System.out.println("接收到的 cartItemIds: " + cartItemIds);

        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录，请先登录。");
        }

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return AjaxResult.error("请至少选择一件商品进行结算。");
        }

        try {
            Orders newOrder = ordersService.createOrderFromCart(loggedInUser.getUid(), cartItemIds);
            AjaxResult successResult = AjaxResult.right("订单创建成功！");
            successResult.put("orderId", newOrder.getOid());
            return successResult;
        } catch (IllegalArgumentException e) {
            // 建议在这里也添加日志记录
            // e.printStackTrace(); 
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            // 打印完整的异常堆栈到控制台，这对于调试至关重要
            System.err.println("捕获到创建订单过程中的异常:");
            e.printStackTrace(); 
            
            String detailedMessage = e.getMessage();
            if (detailedMessage == null || detailedMessage.trim().isEmpty()) {
                detailedMessage = e.getClass().getName(); // 如果原始消息为空，则使用异常类名
            }
            return AjaxResult.error("订单创建失败: " + detailedMessage);
        }
    }
}
