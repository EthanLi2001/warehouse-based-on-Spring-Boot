package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;

public interface InventoryService extends IService<Inventory> {

    boolean updateStock(Integer productId, Integer quantity, String batchNo);

    Inventory getByProductId(Integer productId);
    
    boolean updateMinMaxStock(Integer productId, Integer minStock, Integer maxStock);
}