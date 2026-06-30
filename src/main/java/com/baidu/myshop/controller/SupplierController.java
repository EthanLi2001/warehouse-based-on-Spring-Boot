package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.Supplier;
import com.baidu.myshop.service.SupplierService;
import com.baidu.myshop.utils.AjaxResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/toList")
    public String toList() {
        return "supplier/list";
    }

    @GetMapping("/getPage")
    @ResponseBody
    public Map<String, Object> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String supplierName) {

        Page<Supplier> pageParam = new Page<>(page, limit);
        QueryWrapper<Supplier> wrapper = new QueryWrapper<>();

        if (supplierName != null && !supplierName.isEmpty()) {
            wrapper.like("supplierName", supplierName);
        }

        wrapper.orderByDesc("createTime");
        IPage<Supplier> pageResult = supplierService.page(pageParam, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", pageResult.getTotal());
        result.put("data", pageResult.getRecords());
        return result;
    }

    @GetMapping("/toAdd")
    public String toAdd() {
        return "supplier/add";
    }

    @PostMapping("/doAdd")
    @ResponseBody
    public AjaxResult doAdd(Supplier supplier) {
        supplier.setCreateTime(new Date());
        supplier.setUpdateTime(new Date());
        boolean success = supplierService.save(supplier);
        if (success) {
            return AjaxResult.right("添加成功");
        }
        return AjaxResult.error("添加失败");
    }

    @GetMapping("/toEdit/{supplierId}")
    public String toEdit(@PathVariable Integer supplierId, Model model) {
        Supplier supplier = supplierService.getById(supplierId);
        model.addAttribute("supplier", supplier);
        return "supplier/edit";
    }

    @PostMapping("/doEdit")
    @ResponseBody
    public AjaxResult doEdit(Supplier supplier) {
        supplier.setUpdateTime(new Date());
        boolean success = supplierService.updateById(supplier);
        if (success) {
            return AjaxResult.right("修改成功");
        }
        return AjaxResult.error("修改失败");
    }

    @PostMapping("/delete/{supplierId}")
    @ResponseBody
    public AjaxResult delete(@PathVariable Integer supplierId) {
        boolean success = supplierService.removeById(supplierId);
        if (success) {
            return AjaxResult.right("删除成功");
        }
        return AjaxResult.error("删除失败");
    }

    @PostMapping("/batchDelete")
    @ResponseBody
    public AjaxResult batchDelete(@RequestBody List<Integer> supplierIds) {
        boolean success = supplierService.removeByIds(supplierIds);
        if (success) {
            return AjaxResult.right("批量删除成功");
        }
        return AjaxResult.error("批量删除失败");
    }

    @GetMapping("/getAll")
    @ResponseBody
    public List<Supplier> getAll() {
        return supplierService.getAllSuppliers();
    }
}