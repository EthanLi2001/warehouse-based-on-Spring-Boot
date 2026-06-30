package com.baidu.myshop.service;

import com.baidu.myshop.pojo.PurchaseOrder;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PurchaseOrderService extends IService<PurchaseOrder> {
    String generatePoNo();
}