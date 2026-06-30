package com.baidu.myshop.service;

import com.baidu.myshop.pojo.InventoryCheck;
import com.baidu.myshop.pojo.InventoryCheckRecord;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface InventoryCheckService extends IService<InventoryCheck> {

    String generateCheckNo();

    boolean createCheckTask(InventoryCheck check);

    boolean completeCheck(Integer checkId);

    List<InventoryCheckRecord> getRecordsByCheckId(Integer checkId);

    boolean addRecord(InventoryCheckRecord record);

    boolean updateRecord(InventoryCheckRecord record);

    PageResult<InventoryCheck> getPage(Integer page, Integer limit, String checkNo);

    Map<String, Object> getCheckResult(Integer checkId);

    boolean adjustInventory(Integer checkId);
}