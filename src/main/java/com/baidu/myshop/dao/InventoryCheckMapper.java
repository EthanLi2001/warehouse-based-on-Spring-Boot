package com.baidu.myshop.dao;

import com.baidu.myshop.pojo.InventoryCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {

    @org.apache.ibatis.annotations.Update("UPDATE t_inventory_check SET endTime = #{endTime}, status = #{status} WHERE check_id = #{checkId}")
    int updateStatus(@org.apache.ibatis.annotations.Param("checkId") Integer checkId, 
                     @org.apache.ibatis.annotations.Param("endTime") java.util.Date endTime, 
                     @org.apache.ibatis.annotations.Param("status") Integer status);
}