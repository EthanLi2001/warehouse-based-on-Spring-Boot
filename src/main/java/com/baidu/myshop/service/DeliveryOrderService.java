package com.baidu.myshop.service;

import com.baidu.myshop.pojo.DeliveryOrder;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeliveryOrderService extends IService<DeliveryOrder> {

    String generateDeliveryNo();

    boolean createDeliveryOrder(DeliveryOrder deliveryOrder);

    boolean confirmDelivery(Integer deliveryId);

    PageResult<DeliveryOrder> getPage(Integer page, Integer limit, String orderNo, Integer status);

    List<DeliveryOrder> getByOrderId(Integer orderId);
}
