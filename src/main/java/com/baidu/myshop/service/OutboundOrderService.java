package com.baidu.myshop.service;

import com.baidu.myshop.pojo.OutboundOrder;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OutboundOrderService extends IService<OutboundOrder> {

    String generateOrderNo();

    boolean createOutboundOrder(OutboundOrder outboundOrder);

    boolean approveOutbound(Integer outboundId);

    PageResult<OutboundOrder> getPage(Integer page, Integer limit, Integer type, String orderNo);
}