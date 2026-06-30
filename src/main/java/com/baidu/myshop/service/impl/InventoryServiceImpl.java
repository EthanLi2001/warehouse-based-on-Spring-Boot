package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.InventoryMapper;
import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.service.InventoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Override
    @Transactional
    public boolean updateStock(Integer productId, Integer quantity, String batchNo) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productId", productId);
        
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        
        if (inventory != null) {
            inventory.setQuantity(inventory.getQuantity() + quantity);
            if (batchNo != null && !batchNo.isEmpty()) {
                inventory.setBatchNo(batchNo);
            }
            inventory.setUpdateTime(new Date());
            return baseMapper.updateById(inventory) > 0;
        } else {
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setQuantity(quantity);
            newInventory.setBatchNo(batchNo);
            newInventory.setMinStock(0);
            newInventory.setMaxStock(1000);
            newInventory.setCreateTime(new Date());
            newInventory.setUpdateTime(new Date());
            return baseMapper.insert(newInventory) > 0;
        }
    }

    @Override
    public Inventory getByProductId(Integer productId) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productId", productId);
        return baseMapper.selectOne(queryWrapper);
    }
    
    @Override
    @Transactional
    public boolean updateMinMaxStock(Integer productId, Integer minStock, Integer maxStock) {
        System.out.println("=== InventoryServiceImpl: updateMinMaxStock ===");
        System.out.println("productId: " + productId + ", minStock: " + minStock + ", maxStock: " + maxStock);
        
        if (productId == null) {
            System.out.println("productId 为 null，无法更新");
            return false;
        }
        
        int result = baseMapper.updateMinMaxStock(productId, minStock, maxStock);
        System.out.println("更新结果: " + result);
        
        if (result == 0) {
            System.out.println("警告：没有更新任何记录，请检查 productId 是否存在");
        }
        
        return result > 0;
    }
}