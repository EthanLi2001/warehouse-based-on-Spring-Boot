package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SupplierService extends IService<Supplier> {

    List<Supplier> getAllSuppliers();
}