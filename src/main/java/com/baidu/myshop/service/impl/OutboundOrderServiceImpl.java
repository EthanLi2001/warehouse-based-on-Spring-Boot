package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.DeliveryOrderMapper;
import com.baidu.myshop.dao.InventoryMapper;
import com.baidu.myshop.dao.OrdersMapper;
import com.baidu.myshop.dao.OutboundOrderMapper;
import com.baidu.myshop.dao.ProductMapper;
import com.baidu.myshop.dao.UserMapper;
import com.baidu.myshop.pojo.DeliveryOrder;
import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.pojo.OutboundOrder;
import com.baidu.myshop.service.OutboundOrderService;
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
public class OutboundOrderServiceImpl extends ServiceImpl<OutboundOrderMapper, OutboundOrder> implements OutboundOrderService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Override
    public String generateOrderNo() {
        String prefix = "CK";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return prefix + timestamp.substring(6) + random;
    }

    @Override
    @Transactional
    public boolean createOutboundOrder(OutboundOrder outboundOrder) {
        outboundOrder.setOrderNo(generateOrderNo());
        outboundOrder.setCreateTime(new Date());
        
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productId", outboundOrder.getProductId());
        Inventory inventory = inventoryMapper.selectOne(queryWrapper);
        
        if (inventory == null || inventory.getQuantity() < outboundOrder.getQuantity()) {
            throw new RuntimeException("库存不足");
        }
        
        inventory.setQuantity(inventory.getQuantity() - outboundOrder.getQuantity());
        inventory.setUpdateTime(new Date());
        inventoryMapper.updateById(inventory);
        
        outboundOrder.setStatus(1);
        baseMapper.insert(outboundOrder);
        
        if (outboundOrder.getOrderId() != null) {
            Orders order = new Orders();
            order.setOid(outboundOrder.getOrderId());
            order.setStatus(3);
            ordersMapper.updateById(order);
            
            List<DeliveryOrder> existingDeliveries = deliveryOrderMapper.selectList(
                    new QueryWrapper<DeliveryOrder>().eq("orderId", outboundOrder.getOrderId()));
            if (existingDeliveries == null || existingDeliveries.isEmpty()) {
                DeliveryOrder deliveryOrder = new DeliveryOrder();
                deliveryOrder.setDeliveryNo("DH" + System.currentTimeMillis());
                deliveryOrder.setOrderId(outboundOrder.getOrderId());
                deliveryOrder.setCreateTime(new Date());
                deliveryOrder.setStatus(1);
                deliveryOrder.setOperator(outboundOrder.getOperator());
                deliveryOrderMapper.insert(deliveryOrder);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean approveOutbound(Integer outboundId) {
        OutboundOrder outboundOrder = baseMapper.selectById(outboundId);
        if (outboundOrder != null && outboundOrder.getStatus() == 0) {
            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("productId", outboundOrder.getProductId());
            Inventory inventory = inventoryMapper.selectOne(queryWrapper);
            
            if (inventory != null && inventory.getQuantity() >= outboundOrder.getQuantity()) {
                inventory.setQuantity(inventory.getQuantity() - outboundOrder.getQuantity());
                inventory.setUpdateTime(new Date());
                inventoryMapper.updateById(inventory);
                
                outboundOrder.setStatus(1);
                baseMapper.updateById(outboundOrder);
                
                if (outboundOrder.getOrderId() != null) {
                    Orders order = new Orders();
                    order.setOid(outboundOrder.getOrderId());
                    order.setStatus(3);
                    ordersMapper.updateById(order);
                    
                    List<DeliveryOrder> existingDeliveries = deliveryOrderMapper.selectList(
                            new QueryWrapper<DeliveryOrder>().eq("orderId", outboundOrder.getOrderId()));
                    if (existingDeliveries == null || existingDeliveries.isEmpty()) {
                        DeliveryOrder deliveryOrder = new DeliveryOrder();
                        deliveryOrder.setDeliveryNo("DH" + System.currentTimeMillis());
                        deliveryOrder.setOrderId(outboundOrder.getOrderId());
                        deliveryOrder.setCreateTime(new Date());
                        deliveryOrder.setStatus(1);
                        deliveryOrder.setOperator(outboundOrder.getOperator());
                        deliveryOrderMapper.insert(deliveryOrder);
                    }
                }
                
                return true;
            }
        }
        return false;
    }

    @Override
    public PageResult<OutboundOrder> getPage(Integer page, Integer limit, Integer type, String orderNo) {
        Page<OutboundOrder> pageInfo = new Page<>(page, limit);
        QueryWrapper<OutboundOrder> queryWrapper = new QueryWrapper<>();
        
        if (type != null) {
            queryWrapper.eq("type", type);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like("orderNo", orderNo);
        }
        
        queryWrapper.orderByDesc("createTime");
        Page<OutboundOrder> resultPage = baseMapper.selectPage(pageInfo, queryWrapper);
        
        for (OutboundOrder outbound : resultPage.getRecords()) {
            if (outbound.getOperator() != null) {
                var user = userMapper.selectById(outbound.getOperator());
                if (user != null) {
                    outbound.setOperatorName(user.getName());
                }
            }
            if (outbound.getProductId() != null) {
                var product = productMapper.selectById(outbound.getProductId());
                if (product != null) {
                    outbound.setProductName(product.getPname());
                }
            }
            if (outbound.getOrderId() != null) {
                var order = ordersMapper.selectById(outbound.getOrderId());
                if (order != null) {
                    outbound.setRelatedOrderNo(order.getOrderNo());
                }
            }
        }
        
        PageResult<OutboundOrder> pageResult = new PageResult<>();
        pageResult.setCode(0);
        pageResult.setMsg("查询成功");
        pageResult.setCount(resultPage.getTotal());
        pageResult.setData(resultPage.getRecords());
        return pageResult;
    }
}