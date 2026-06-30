package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.InboundOrderMapper;
import com.baidu.myshop.pojo.InboundOrder;
import com.baidu.myshop.service.InboundOrderService;
import com.baidu.myshop.service.InventoryService;
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
public class InboundOrderServiceImpl extends ServiceImpl<InboundOrderMapper, InboundOrder> implements InboundOrderService {

    @Autowired
    private InventoryService inventoryService;

    @Override
    public String generateOrderNo() {
        String prefix = "RK";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return prefix + timestamp.substring(6) + random;
    }

    @Override
    @Transactional
    public boolean createInboundOrder(InboundOrder inboundOrder) {
        inboundOrder.setOrderNo(generateOrderNo());
        inboundOrder.setCreateTime(new Date());
        inboundOrder.setStatus(0);
        return baseMapper.insert(inboundOrder) > 0;
    }

    @Override
    @Transactional
    public boolean approveInbound(Integer inboundId) {
        InboundOrder inboundOrder = baseMapper.selectById(inboundId);
        if (inboundOrder != null && inboundOrder.getStatus() == 0) {
            boolean success = inventoryService.updateStock(
                inboundOrder.getProductId(),
                inboundOrder.getQuantity(),
                inboundOrder.getBatchNo()
            );
            if (success) {
                inboundOrder.setStatus(1);
                baseMapper.updateById(inboundOrder);
                return true;
            }
        }
        return false;
    }

    @Override
    public PageResult<InboundOrder> getPage(Integer page, Integer limit, Integer type, String orderNo) {
        Page<InboundOrder> pageInfo = new Page<>(page, limit);
        QueryWrapper<InboundOrder> queryWrapper = new QueryWrapper<>();
        
        if (type != null) {
            queryWrapper.eq("type", type);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like("orderNo", orderNo);
        }
        
        queryWrapper.orderByDesc("createTime");
        Page<InboundOrder> resultPage = baseMapper.selectPage(pageInfo, queryWrapper);
        
        PageResult<InboundOrder> pageResult = new PageResult<>();
        pageResult.setCode(0);
        pageResult.setMsg("查询成功");
        pageResult.setCount(resultPage.getTotal());
        pageResult.setData(resultPage.getRecords());
        return pageResult;
    }
}