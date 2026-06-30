package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.InventoryCheck;
import com.baidu.myshop.pojo.InventoryCheckRecord;
import com.baidu.myshop.pojo.dto.UpdateRecordRequest;
import com.baidu.myshop.service.InventoryCheckService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inventory/check")
public class InventoryCheckController {

    @Autowired
    private InventoryCheckService inventoryCheckService;

    @RequestMapping("/toList")
    public String toList() {
        return "inventory/check/list";
    }

    @RequestMapping("/toAdd")
    public String toAdd() {
        return "inventory/check/add";
    }

    @RequestMapping("/toRecord/{checkId}")
    public String toRecord(@PathVariable Integer checkId, Model model) {
        Map<String, Object> result = inventoryCheckService.getCheckResult(checkId);
        model.addAttribute("check", result.get("check"));
        model.addAttribute("records", result.get("records"));
        return "inventory/check/record";
    }

    @RequestMapping("/toResult/{checkId}")
    public String toResult(@PathVariable Integer checkId, Model model) {
        Map<String, Object> result = inventoryCheckService.getCheckResult(checkId);
        model.addAttribute("check", result.get("check"));
        model.addAttribute("records", result.get("records"));
        model.addAttribute("totalDiff", result.get("totalDiff"));
        model.addAttribute("totalExpected", result.get("totalExpected"));
        model.addAttribute("totalActual", result.get("totalActual"));
        return "inventory/check/result";
    }

    @PostMapping("/list")
    @ResponseBody
    public PageResult<InventoryCheck> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            String checkNo) {
        return inventoryCheckService.getPage(page, limit, checkNo);
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody InventoryCheck check, HttpSession session) {
        try {
            com.baidu.myshop.pojo.User user2 = (com.baidu.myshop.pojo.User) session.getAttribute("user2");
            if (user2 != null && user2.getUid() != null) {
                check.setOperator(String.valueOf(user2.getUid()));
            }
            // 自动设置开始时间和结束时间
            if (check.getStartTime() == null) {
                check.setStartTime(new java.util.Date());
            }
            if (check.getEndTime() == null) {
                check.setEndTime(new java.util.Date());
            }
            boolean success = inventoryCheckService.createCheckTask(check);
            if (success) {
                return AjaxResult.right("创建成功");
            }
            return AjaxResult.error("创建失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/complete/{checkId}")
    @ResponseBody
    public AjaxResult complete(@PathVariable Integer checkId) {
        try {
            boolean success = inventoryCheckService.completeCheck(checkId);
            if (success) {
                return AjaxResult.right("盘点完成");
            }
            return AjaxResult.error("盘点失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/updateRecord")
    @ResponseBody
    public AjaxResult updateRecord(@RequestBody UpdateRecordRequest request) {
        System.out.println("=== InventoryCheckController: updateRecord 方法被调用 ===");
        System.out.println("接收到的数据: recordId=" + request.getRecordId() + ", actualQty=" + request.getActualQty());
        
        try {
            InventoryCheckRecord record = new InventoryCheckRecord();
            record.setRecordId(request.getRecordId());
            record.setActualQty(request.getActualQty());
            
            boolean success = inventoryCheckService.updateRecord(record);
            if (success) {
                System.out.println("更新成功");
                return AjaxResult.right("更新成功");
            }
            System.out.println("更新失败");
            return AjaxResult.error("更新失败");
        } catch (Exception e) {
            System.out.println("系统异常: " + e.getMessage());
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/delete/{checkId}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer checkId) {
        try {
            boolean success = inventoryCheckService.removeById(checkId);
            if (success) {
                return AjaxResult.right("删除成功");
            }
            return AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @GetMapping("/getRecords/{checkId}")
    @ResponseBody
    public List<InventoryCheckRecord> getRecords(@PathVariable Integer checkId) {
        return inventoryCheckService.getRecordsByCheckId(checkId);
    }

    @PostMapping("/adjustInventory/{checkId}")
    @ResponseBody
    public AjaxResult adjustInventory(@PathVariable Integer checkId) {
        try {
            boolean success = inventoryCheckService.adjustInventory(checkId);
            if (success) {
                return AjaxResult.right("库存调整成功");
            }
            return AjaxResult.error("库存调整失败");
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }
}