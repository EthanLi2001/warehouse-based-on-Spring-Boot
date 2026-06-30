package com.baidu.myshop.service;

import com.baidu.myshop.pojo.PurchasePlan;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PurchasePlanService extends IService<PurchasePlan> {
    String generatePlanNo();
}