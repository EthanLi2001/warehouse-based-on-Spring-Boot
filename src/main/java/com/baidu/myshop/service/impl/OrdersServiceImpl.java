package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.CartMapper;
import com.baidu.myshop.dao.CustomerMapper;
import com.baidu.myshop.dao.DeliveryOrderMapper;
import com.baidu.myshop.dao.OrderDetailMapper;
import com.baidu.myshop.dao.OutboundOrderMapper;
import com.baidu.myshop.dao.OrdersMapper;
import com.baidu.myshop.dao.ProductMapper;
import com.baidu.myshop.pojo.Cart;
import com.baidu.myshop.pojo.Customer;
import com.baidu.myshop.pojo.DeliveryOrder;
import com.baidu.myshop.pojo.OutboundOrder;
import com.baidu.myshop.pojo.OrderDetail;
import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.service.OrdersService;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单管理表 服务实现类
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;
    @Autowired
    private OutboundOrderMapper outboundOrderMapper;

    /**
     * 查询所有订单信息
     *
     * @param oid 订单ID,用于模糊查询
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public PageResult<Orders> getAllOrders(String oid, Integer currentPage, Integer pageSize) {
        Page<Orders> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        if (oid != null && !oid.trim().isEmpty()) {
            wrapper.like("oid", oid);
        }
        List<Orders> orders = ordersMapper.selectList(page, wrapper);
        orders.forEach(item -> {
            if (item.getCustomerId() != null) {
                Customer customer = customerMapper.selectById(item.getCustomerId());
                if (customer != null) {
                    item.setUserName(customer.getCustomerName());
                }
            }
            
            List<OrderDetail> details = orderDetailMapper.selectList(
                    new QueryWrapper<OrderDetail>().eq("oid", item.getOid()));
            StringBuilder productNames = new StringBuilder();
            for (OrderDetail detail : details) {
                Product product = productMapper.selectById(detail.getPid());
                if (product != null) {
                    if (productNames.length() > 0) {
                        productNames.append(", ");
                    }
                    productNames.append(product.getPname());
                    if (detail.getQuantity() != null && detail.getQuantity() > 1) {
                        productNames.append("x").append(detail.getQuantity());
                    }
                }
            }
            item.setProductNames(productNames.toString());
            
            List<DeliveryOrder> deliveryOrders = deliveryOrderMapper.selectList(
                    new QueryWrapper<DeliveryOrder>().eq("orderId", item.getOid()));
            if (deliveryOrders != null && !deliveryOrders.isEmpty()) {
                item.setDeliveryNo(deliveryOrders.get(0).getDeliveryNo());
            }
            
            List<OutboundOrder> outboundOrders = outboundOrderMapper.selectList(
                    new QueryWrapper<OutboundOrder>().eq("orderId", item.getOid()));
            if (outboundOrders != null && !outboundOrders.isEmpty()) {
                item.setOutboundNo(outboundOrders.get(0).getOrderNo());
            }
        });
        Long l = ordersMapper.selectCount(wrapper);
        PageResult<Orders> ordersPageResult = new PageResult<>();
        ordersPageResult.setCode(0);
        ordersPageResult.setData(orders);
        ordersPageResult.setMsg("分页成功");
        ordersPageResult.setCount(l);

        return ordersPageResult;
    }

    /**
     * 批量删除
     *
     * @param orderIds
     * @return
     */
    @Override
    @Transactional
    public int deleteOrdersByIds(Integer[] orderIds) {
        List<Integer> orderIdList = Arrays.asList(orderIds);
        
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.in("oid", orderIdList);
        orderDetailMapper.delete(detailWrapper);
        
        QueryWrapper<DeliveryOrder> deliveryWrapper = new QueryWrapper<>();
        deliveryWrapper.in("orderId", orderIdList);
        deliveryOrderMapper.delete(deliveryWrapper);
        
        QueryWrapper<OutboundOrder> outboundWrapper = new QueryWrapper<>();
        outboundWrapper.in("orderId", orderIdList);
        outboundOrderMapper.delete(outboundWrapper);
        
        return ordersMapper.deleteBatchIds(orderIdList);
    }

    /**
     * 根据id删除
     *
     * @param oid
     * @return
     */
    @Override
    @Transactional
    public int deleteOrderById(Integer oid) {
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("oid", oid);
        orderDetailMapper.delete(detailWrapper);
        
        QueryWrapper<DeliveryOrder> deliveryWrapper = new QueryWrapper<>();
        deliveryWrapper.eq("orderId", oid);
        deliveryOrderMapper.delete(deliveryWrapper);
        
        QueryWrapper<OutboundOrder> outboundWrapper = new QueryWrapper<>();
        outboundWrapper.eq("orderId", oid);
        outboundOrderMapper.delete(outboundWrapper);
        
        return ordersMapper.deleteById(oid);
    }

    /**
     * 新增订单
     *
     * @param orders
     * @return
     */
    @Override
    public int insertOrder(Orders orders) {
        orders.setOrderTime(new Date());
        if (orders.getStatus() == null) {
            orders.setStatus(0);
        }
        orders.setOrderNo(generateOrderNo());
        return ordersMapper.insert(orders);
    }
    
    private String generateOrderNo() {
        return "XS" + System.currentTimeMillis();
    }

    /**
     * 根据id查询
     *
     * @param oid
     * @return
     */
    @Override
    public Orders getOrderByOid(Integer oid) {
        return ordersMapper.selectById(oid);
    }

    /**
     * 修改订单
     *
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public int updateOrder(Orders orders) {
        return ordersMapper.updateById(orders);
    }

    @Override
    @Transactional
    public Orders createOrderFromCart(Integer userId, List<Integer> cartItemIds) throws Exception {
        if (userId == null || cartItemIds == null || cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("用户ID和购物车项ID列表不能为空");
        }

        List<Cart> validCartItems = new ArrayList<>();
        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (Integer cartId : cartItemIds) {
            Cart cartItem = cartMapper.selectById(cartId);
            if (cartItem == null || !cartItem.getUid().equals(userId)) {
                log.warn("尝试操作不属于用户 " + userId + " 的购物车项 " + cartId + " 或购物车项不存在");
                continue;
            }

            Product product = productMapper.selectById(cartItem.getPid());
            if (product == null || product.getPrice() == null) {
                throw new Exception("购物车中的商品ID " + cartItem.getPid() + " 无效或价格未设置");
            }

            validCartItems.add(cartItem);
            BigDecimal itemPrice = product.getPrice();
            BigDecimal quantity = new BigDecimal(cartItem.getQuantity());
            totalOrderPrice = totalOrderPrice.add(itemPrice.multiply(quantity));
        }

        if (validCartItems.isEmpty()) {
            throw new Exception("没有有效的购物车项来创建订单");
        }

        Orders newOrder = new Orders();
        newOrder.setCustomerId(userId);
        newOrder.setStatus(0);
        newOrder.setTotalPrice(totalOrderPrice);
        newOrder.setOrderTime(new Date());

        int insertedOrderCount = ordersMapper.insert(newOrder);
        if (insertedOrderCount == 0 || newOrder.getOid() == null) {
            throw new Exception("创建订单失败，无法保存订单头信息");
        }

        for (Cart cartItem : validCartItems) {
            Product product = productMapper.selectById(cartItem.getPid());

            OrderDetail detail = new OrderDetail();
            detail.setOid(newOrder.getOid());
            detail.setPid(cartItem.getPid());
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(product.getPrice());

            orderDetailMapper.insert(detail);
        }

        cartMapper.deleteBatchIds(validCartItems.stream().map(Cart::getCid).collect(java.util.stream.Collectors.toList()));

        return newOrder;
    }

    @Override
    public Integer getLastOrderId() {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("oid").last("LIMIT 1");
        Orders orders = ordersMapper.selectOne(wrapper);
        return orders != null ? orders.getOid() : null;
    }
}
