package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.PurchasePlanMapper;
import com.baidu.myshop.pojo.PurchasePlan;
import com.baidu.myshop.service.PurchasePlanService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class PurchasePlanServiceImpl extends ServiceImpl<PurchasePlanMapper, PurchasePlan> implements PurchasePlanService {

    @Autowired
    private PurchasePlanMapper purchasePlanMapper;

    @Override
    public String generatePlanNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "CGJH" + dateStr;
        
        QueryWrapper<PurchasePlan> wrapper = new QueryWrapper<>();
        wrapper.likeRight("planNo", prefix);
        wrapper.orderByDesc("planNo");
        wrapper.last("LIMIT 1");
        
        PurchasePlan lastPlan = purchasePlanMapper.selectOne(wrapper);
        
        String sequence = "0001";
        if (lastPlan != null && lastPlan.getPlanNo() != null) {
            String lastNo = lastPlan.getPlanNo();
            sequence = String.format("%04d", Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1);
        }
        
        return prefix + sequence;
    }
}