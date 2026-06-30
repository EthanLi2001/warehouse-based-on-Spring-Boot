package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.InventoryCheckMapper;
import com.baidu.myshop.dao.InventoryCheckRecordMapper;
import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.pojo.InventoryCheck;
import com.baidu.myshop.pojo.InventoryCheckRecord;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.service.InventoryCheckService;
import com.baidu.myshop.service.InventoryService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

@Service
public class InventoryCheckServiceImpl extends ServiceImpl<InventoryCheckMapper, InventoryCheck> implements InventoryCheckService {

    @Autowired
    private InventoryCheckRecordMapper recordMapper;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private com.baidu.myshop.dao.InventoryMapper inventoryMapper;

    @Override
    public String generateCheckNo() {
        String prefix = "PD";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return prefix + timestamp.substring(6) + random;
    }

    @Override
    @Transactional
    public boolean createCheckTask(InventoryCheck check) {
        check.setCheckNo(generateCheckNo());
        check.setStartTime(new Date());
        check.setStatus(0);
        boolean success = baseMapper.insert(check) > 0;
        
        if (success) {
            List<Inventory> inventories = inventoryService.list();
            for (Inventory inv : inventories) {
                InventoryCheckRecord record = new InventoryCheckRecord();
                record.setCheckId(check.getCheckId());
                record.setProductId(inv.getProductId());
                record.setExpectedQty(inv.getQuantity());
                record.setActualQty(0);
                record.setDiffQty(0);
                recordMapper.insert(record);
            }
        }
        return success;
    }

    @Override
    @Transactional
    public boolean completeCheck(Integer checkId) {
        InventoryCheck check = baseMapper.selectById(checkId);
        if (check != null && check.getStatus() == 0) {
            List<InventoryCheckRecord> records = recordMapper.selectByCheckId(checkId);
            int totalDiff = 0;
            
            for (InventoryCheckRecord record : records) {
                int diff = record.getActualQty() - record.getExpectedQty();
                record.setDiffQty(diff);
                recordMapper.updateRecordById(record.getRecordId(), record.getActualQty(), diff);
                totalDiff += diff;
            }
            
            check.setEndTime(new Date());
            check.setStatus(1);
            baseMapper.updateStatus(check.getCheckId(), check.getEndTime(), check.getStatus());
            return true;
        }
        return false;
    }

    @Override
    public List<InventoryCheckRecord> getRecordsByCheckId(Integer checkId) {
        return recordMapper.selectByCheckId(checkId);
    }

    @Override
    public boolean addRecord(InventoryCheckRecord record) {
        return recordMapper.insert(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(InventoryCheckRecord record) {
        System.out.println("=== updateRecord 方法被调用 ===");
        System.out.println("recordId: " + record.getRecordId() + ", actualQty: " + record.getActualQty());
        
        InventoryCheckRecord existing = recordMapper.selectRecordById(record.getRecordId());
        System.out.println("查询到的记录: " + existing);
        
        if (existing != null) {
            System.out.println("existing.getRecordId(): " + existing.getRecordId());
            System.out.println("existing.getCheckId(): " + existing.getCheckId());
            System.out.println("existing.getExpectedQty(): " + existing.getExpectedQty());
            System.out.println("existing.getActualQty(): " + existing.getActualQty());
            
            int diff = record.getActualQty() - existing.getExpectedQty();
            System.out.println("计算差异: expectedQty=" + existing.getExpectedQty() + ", actualQty=" + record.getActualQty() + ", diff=" + diff);
            
            int result = recordMapper.updateRecordById(record.getRecordId(), record.getActualQty(), diff);
            System.out.println("updateRecordById 返回值: " + result);
            
            InventoryCheckRecord updated = recordMapper.selectRecordById(record.getRecordId());
            System.out.println("更新后的记录: " + updated);
            if (updated != null) {
                System.out.println("updated.getActualQty(): " + updated.getActualQty());
                System.out.println("updated.getDiffQty(): " + updated.getDiffQty());
            }
            
            return result > 0;
        }
        System.out.println("未找到记录");
        return false;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable checkId) {
        recordMapper.deleteByCheckId((Integer) checkId);
        return baseMapper.deleteById(checkId) > 0;
    }

    @Override
    public PageResult<InventoryCheck> getPage(Integer page, Integer limit, String checkNo) {
        Page<InventoryCheck> pageInfo = new Page<>(page, limit);
        QueryWrapper<InventoryCheck> queryWrapper = new QueryWrapper<>();
        
        if (checkNo != null && !checkNo.isEmpty()) {
            queryWrapper.like("checkNo", checkNo);
        }
        
        queryWrapper.orderByDesc("startTime");
        Page<InventoryCheck> resultPage = baseMapper.selectPage(pageInfo, queryWrapper);
        
        PageResult<InventoryCheck> pageResult = new PageResult<>();
        pageResult.setCode(0);
        pageResult.setMsg("查询成功");
        pageResult.setCount(resultPage.getTotal());
        pageResult.setData(resultPage.getRecords());
        return pageResult;
    }

    @Override
    @Transactional
    public boolean adjustInventory(Integer checkId) {
        System.out.println("=== InventoryCheckServiceImpl: adjustInventory 方法被调用 ===");
        System.out.println("checkId: " + checkId);
        
        InventoryCheck check = baseMapper.selectById(checkId);
        System.out.println("check: " + check);
        
        if (check != null && check.getStatus() == 1) {
            System.out.println("盘点状态为已完成，开始调整库存...");
            
            List<InventoryCheckRecord> records = recordMapper.selectByCheckId(checkId);
            System.out.println("盘点记录数量: " + records.size());
            
            for (InventoryCheckRecord record : records) {
                System.out.println("record: productId=" + record.getProductId() + ", diffQty=" + record.getDiffQty());
                
                if (record.getDiffQty() != 0) {
                    Inventory inventory = inventoryService.getByProductId(record.getProductId());
                    System.out.println("inventory: " + inventory);
                    
                    if (inventory != null) {
                        int newQty = inventory.getQuantity() + record.getDiffQty();
                        System.out.println("原库存: " + inventory.getQuantity() + ", 差异: " + record.getDiffQty() + ", 新库存: " + newQty);
                        
                        if (newQty < 0) {
                            throw new RuntimeException("库存调整后数量不能为负数");
                        }
                        
                        int updateResult = inventoryMapper.updateQuantity(inventory.getInventoryId(), newQty);
                        System.out.println("库存更新结果: " + updateResult);
                    }
                }
            }
            
            check.setStatus(2);
            baseMapper.updateStatus(check.getCheckId(), check.getEndTime(), check.getStatus());
            System.out.println("盘点状态更新为已调整");
            return true;
        } else {
            System.out.println("盘点不存在或状态不是已完成");
        }
        return false;
    }

    @Override
    public Map<String, Object> getCheckResult(Integer checkId) {
        Map<String, Object> result = new HashMap<>();
        InventoryCheck check = baseMapper.selectById(checkId);
        result.put("check", check);
        
        List<InventoryCheckRecord> records = recordMapper.selectByCheckId(checkId);
        List<Map<String, Object>> recordList = new ArrayList<>();
        
        int totalDiff = 0;
        int totalExpected = 0;
        int totalActual = 0;
        
        for (InventoryCheckRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("record", record);
            
            Product product = productService.getById(record.getProductId());
            item.put("productName", product != null ? product.getPname() : "未知");
            item.put("productId", record.getProductId());
            
            if (record.getDiffQty() == 0) {
                item.put("statusText", "一致");
            } else if (record.getDiffQty() > 0) {
                item.put("statusText", "盘盈");
            } else {
                item.put("statusText", "盘亏");
            }
            
            totalDiff += record.getDiffQty();
            totalExpected += record.getExpectedQty();
            totalActual += record.getActualQty();
            
            recordList.add(item);
        }
        
        result.put("records", recordList);
        result.put("totalDiff", totalDiff);
        result.put("totalExpected", totalExpected);
        result.put("totalActual", totalActual);
        
        return result;
    }
}