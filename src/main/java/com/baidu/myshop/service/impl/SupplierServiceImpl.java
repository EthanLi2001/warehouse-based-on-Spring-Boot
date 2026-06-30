package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.SupplierMapper;
import com.baidu.myshop.pojo.Supplier;
import com.baidu.myshop.service.SupplierService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public List<Supplier> getAllSuppliers() {
        return baseMapper.selectList(null);
    }
}