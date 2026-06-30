package com.baidu.myshop.controller;


import ch.qos.logback.core.model.Model;
import com.baidu.myshop.pojo.Role;
import com.baidu.myshop.service.RoleService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaoye
 * @since 2025-04-15
 */
@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/toAddRole")
    public String toAddRole(){
        return "role/add";
    }
    @RequestMapping("/toRoleList")
    public String toList(){
        return "role/list";
    }

    @RequestMapping("/toEditRole/{rid}")
    public String toUpdateRole(@PathVariable Integer rid, HttpServletRequest request  ){
        Role role = roleService.getById(rid);
        request.setAttribute("role", role); // 将角色列表添加到模型中
        return "role/edit";
    }
    @PostMapping("/editRole")
    @ResponseBody
    public AjaxResult editRole(Role role){
        boolean flag = roleService.updateById(role);
        if(flag){
            return new AjaxResult();
        }
        return AjaxResult.error();
    }

    @PostMapping("/batchRole")
    @ResponseBody
    public AjaxResult batchDeleteUsers(@RequestBody Map<String, List<Long>> params) {
        List<Long> rid = params.get("rid");
        if (rid == null || rid.isEmpty()) {
            return AjaxResult.error("请选择要删除的数据");
        }
        boolean success = roleService.removeByIds(rid);
        if (success) {
            return new AjaxResult();
        } else {
            return AjaxResult.error("批量删除失败");
        }
    }
    @PostMapping("/roleList")
    @ResponseBody
    public PageResult<Role> getPage(String pname, @RequestParam(defaultValue = "1")Integer page,
                                    @RequestParam(defaultValue = "5")  Integer limit){
        System.out.println("------getPage-------");
        if ("".equals(pname)){
            pname=null;
        }
        PageResult<Role> result= roleService.getPage(pname,page,limit);
        return result;
    }
    @RequestMapping("/addRole")
    @ResponseBody // 明确指出该方法返回的是响应体内容，而非视图名
    public AjaxResult addRole( Role  role){
        boolean flag = roleService.save(role);
        if(flag){
            return new AjaxResult();
        }
        return AjaxResult.error();
    }

    @PostMapping("/deleteRoleByRid/{rid}")
    @ResponseBody // 明确指出该方法返回的是响应体内容，而非视图名
    public AjaxResult deleteRoleByRid(@PathVariable Integer rid){
        boolean flag = roleService.removeById(rid);
        if(flag){
            return new AjaxResult();
        }
        return AjaxResult.error();
    }




}
