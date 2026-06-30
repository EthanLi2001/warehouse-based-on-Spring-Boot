package com.baidu.myshop.service;

import com.baidu.myshop.pojo.InboundOrder;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface InboundOrderService extends IService<InboundOrder> {

    String generateOrderNo();

    boolean createInboundOrder(InboundOrder inboundOrder);

    boolean approveInbound(Integer inboundId);

    PageResult<InboundOrder> getPage(Integer page, Integer limit, Integer type, String orderNo);
}