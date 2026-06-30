package com.baidu.myshop.dao;

import com.baidu.myshop.pojo.InventoryCheckRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface InventoryCheckRecordMapper extends BaseMapper<InventoryCheckRecord> {

    @Select("SELECT record_id as recordId, checkId, productId, expectedQty, actualQty, diffQty FROM t_inventory_check_record WHERE checkId = #{checkId}")
    List<InventoryCheckRecord> selectByCheckId(@Param("checkId") Integer checkId);
    
    @Delete("DELETE FROM t_inventory_check_record WHERE checkId = #{checkId}")
    int deleteByCheckId(@Param("checkId") Integer checkId);
    
    @Update("UPDATE t_inventory_check_record SET actualQty = #{actualQty}, diffQty = #{diffQty} WHERE record_id = #{recordId}")
    int updateRecordById(@Param("recordId") Integer recordId, @Param("actualQty") Integer actualQty, @Param("diffQty") Integer diffQty);
    
    @Select("SELECT record_id as recordId, checkId, productId, expectedQty, actualQty, diffQty FROM t_inventory_check_record WHERE record_id = #{recordId}")
    InventoryCheckRecord selectRecordById(@Param("recordId") Integer recordId);
}