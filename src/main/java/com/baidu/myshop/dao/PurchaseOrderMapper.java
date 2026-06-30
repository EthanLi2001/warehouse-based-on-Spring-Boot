package com.baidu.myshop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baidu.myshop.pojo.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
}