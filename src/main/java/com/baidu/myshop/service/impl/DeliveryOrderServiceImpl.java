package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.DeliveryOrderMapper;
import com.baidu.myshop.dao.InventoryMapper;
import com.baidu.myshop.dao.OrderDetailMapper;
import com.baidu.myshop.dao.OutboundOrderMapper;
import com.baidu.myshop.dao.OrdersMapper;
import com.baidu.myshop.dao.UserMapper;
import com.baidu.myshop.pojo.DeliveryOrder;
import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.pojo.OrderDetail;
import com.baidu.myshop.pojo.OutboundOrder;
import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.service.DeliveryOrderService;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements DeliveryOrderService {

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private OutboundOrderMapper outboundOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String generateDeliveryNo() {
        return "DH" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public boolean createDeliveryOrder(DeliveryOrder deliveryOrder) {
        deliveryOrder.setDeliveryNo(generateDeliveryNo());
        deliveryOrder.setCreateTime(new Date());
        deliveryOrder.setStatus(0);
        
        if (deliveryOrderMapper.insert(deliveryOrder) <= 0) {
            return false;
        }
        
        return confirmDeliveryWithStockCheck(deliveryOrder.getDeliveryId());
    }
    
    @Transactional
    public boolean confirmDeliveryWithStockCheck(Integer deliveryId) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(deliveryId);
        if (deliveryOrder == null || deliveryOrder.getStatus() != 0) {
            return false;
        }

        Integer orderId = deliveryOrder.getOrderId();
        Orders orders = ordersMapper.selectById(orderId);
        if (orders == null) {
            return false;
        }

        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                new QueryWrapper<OrderDetail>().eq("oid", orderId));

        for (OrderDetail detail : orderDetails) {
            Inventory inventory = inventoryMapper.selectOne(
                    new QueryWrapper<Inventory>().eq("productId", detail.getPid()));
            if (inventory == null || inventory.getQuantity() < detail.getQuantity()) {
                throw new RuntimeException("库存不足，无法发货");
            }

            int newQty = inventory.getQuantity() - detail.getQuantity();
            inventoryMapper.updateQuantity(inventory.getInventoryId(), newQty);

            OutboundOrder outboundOrder = new OutboundOrder();
            outboundOrder.setOrderNo("CK" + System.currentTimeMillis());
            outboundOrder.setType(1);
            outboundOrder.setOrderId(orderId);
            outboundOrder.setProductId(detail.getPid());
            outboundOrder.setQuantity(detail.getQuantity());
            outboundOrder.setOperator(deliveryOrder.getOperator());
            outboundOrder.setStatus(1);
            outboundOrder.setCreateTime(new Date());
            outboundOrderMapper.insert(outboundOrder);
        }

        deliveryOrder.setStatus(1);
        deliveryOrderMapper.updateById(deliveryOrder);

        orders.setStatus(3);
        ordersMapper.updateById(orders);

        return true;
    }

    @Override
    @Transactional
    public boolean confirmDelivery(Integer deliveryId) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(deliveryId);
        if (deliveryOrder == null || deliveryOrder.getStatus() != 0) {
            return false;
        }

        Integer orderId = deliveryOrder.getOrderId();
        Orders orders = ordersMapper.selectById(orderId);
        if (orders == null) {
            return false;
        }

        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                new QueryWrapper<OrderDetail>().eq("oid", orderId));

        for (OrderDetail detail : orderDetails) {
            Inventory inventory = inventoryMapper.selectOne(
                    new QueryWrapper<Inventory>().eq("productId", detail.getPid()));
            if (inventory == null || inventory.getQuantity() < detail.getQuantity()) {
                return false;
            }

            int newQty = inventory.getQuantity() - detail.getQuantity();
            inventoryMapper.updateQuantity(inventory.getInventoryId(), newQty);

            OutboundOrder outboundOrder = new OutboundOrder();
            outboundOrder.setOrderNo("CK" + System.currentTimeMillis());
            outboundOrder.setType(1);
            outboundOrder.setOrderId(orderId);
            outboundOrder.setProductId(detail.getPid());
            outboundOrder.setQuantity(detail.getQuantity());
            outboundOrder.setOperator(deliveryOrder.getOperator());
            outboundOrder.setStatus(1);
            outboundOrder.setCreateTime(new Date());
            outboundOrderMapper.insert(outboundOrder);
        }

        deliveryOrder.setStatus(1);
        deliveryOrderMapper.updateById(deliveryOrder);

        orders.setStatus(3);
        ordersMapper.updateById(orders);

        return true;
    }

    @Override
    public PageResult<DeliveryOrder> getPage(Integer page, Integer limit, String orderNo, Integer status) {
        Page<DeliveryOrder> pageObj = new Page<>(page, limit);
        QueryWrapper<DeliveryOrder> wrapper = new QueryWrapper<>();
        
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like("delivery_no", orderNo);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");

        Page<DeliveryOrder> resultPage = deliveryOrderMapper.selectPage(pageObj, wrapper);
        
        for (DeliveryOrder delivery : resultPage.getRecords()) {
            Orders order = ordersMapper.selectById(delivery.getOrderId());
            if (order != null) {
                delivery.setOrderNo(String.valueOf(order.getOid()));
            }

            if (delivery.getOperator() != null) {
                var user = userMapper.selectById(delivery.getOperator());
                if (user != null) {
                    delivery.setOperatorName(user.getName());
                }
            }
        }

        PageResult<DeliveryOrder> pageResult = new PageResult<>();
        pageResult.setCode(0);
        pageResult.setData(resultPage.getRecords());
        pageResult.setMsg("查询成功");
        pageResult.setCount(resultPage.getTotal());
        return pageResult;
    }

    @Override
    public List<DeliveryOrder> getByOrderId(Integer orderId) {
        return deliveryOrderMapper.selectList(new QueryWrapper<DeliveryOrder>().eq("order_id", orderId));
    }
}
