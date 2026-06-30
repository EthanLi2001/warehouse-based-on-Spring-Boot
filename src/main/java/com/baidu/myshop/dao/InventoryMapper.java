package com.baidu.myshop.dao;

import com.baidu.myshop.pojo.Inventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface InventoryMapper extends BaseMapper<Inventory> {
    
    @Update("UPDATE t_inventory SET minStock = #{minStock}, maxStock = #{maxStock}, updateTime = NOW() WHERE productId = #{productId}")
    int updateMinMaxStock(@Param("productId") Integer productId, @Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
    
    @Update("UPDATE t_inventory SET quantity = #{quantity}, updateTime = NOW() WHERE inventory_id = #{inventoryId}")
    int updateQuantity(@Param("inventoryId") Integer inventoryId, @Param("quantity") Integer quantity);
}