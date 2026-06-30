package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.PurchaseOrderMapper;
import com.baidu.myshop.pojo.PurchaseOrder;
import com.baidu.myshop.service.PurchaseOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public String generatePoNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "CGDD" + dateStr;

        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.likeRight("poNo", prefix);
        wrapper.orderByDesc("poNo");
        wrapper.last("LIMIT 1");

        PurchaseOrder lastOrder = purchaseOrderMapper.selectOne(wrapper);

        String sequence = "0001";
        if (lastOrder != null && lastOrder.getPoNo() != null) {
            String lastNo = lastOrder.getPoNo();
            sequence = String.format("%04d", Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1);
        }

        return prefix + sequence;
    }
}