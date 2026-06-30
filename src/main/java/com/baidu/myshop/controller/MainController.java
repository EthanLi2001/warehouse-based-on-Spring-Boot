package com.baidu.myshop.controller;


import com.baidu.myshop.utils.SysMenu;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @RequestMapping("/")
    public String index() {
        return "redirect:/user/toLogin";
    }

    @RequestMapping("/toIndex")
    public String toContent(HttpServletRequest request) {

        String role = (String) request.getSession().getAttribute("role");
        if (role == null) {
            // 未登录，重定向到登录页
            return "redirect:/user/toLogin";
        }
        if ("admin".equals(role)) {
            request.setAttribute("userMenuList", getAdminMenu());
        } else if ("user".equals(role)) {
            request.setAttribute("userMenuList", getUserMenu());
        } else if ("sales".equals(role)) {
            request.setAttribute("userMenuList", getSalesMenu());
        }
        return "content";

    }

    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    // 管理员菜单（仓库端）
    private List<SysMenu> getAdminMenu() {
        List<SysMenu> ml = new ArrayList<>();

        SysMenu m1 = new SysMenu("warehouse", "仓库管理", "layui-icon layui-icon-util", false, null);
        SysMenu m1_1 = new SysMenu("warehouse", "入库管理", "layui-icon layui-icon-set", true, "/inbound/toList");
        SysMenu m1_2 = new SysMenu("warehouse", "出库管理", "layui-icon layui-icon-set", true, "/outbound/toList");
        SysMenu m1_3 = new SysMenu("warehouse", "库存盘点", "layui-icon layui-icon-set", true, "/inventory/check/toList");
        SysMenu m1_4 = new SysMenu("warehouse", "库存预警", "layui-icon layui-icon-set", true, "/inventory/warning/toList");
        SysMenu m1_5 = new SysMenu("warehouse", "商品管理", "layui-icon layui-icon-set", true, "/product/toList");
        m1.getChildMenuList().add(m1_1);
        m1.getChildMenuList().add(m1_2);
        m1.getChildMenuList().add(m1_3);
        m1.getChildMenuList().add(m1_4);
        m1.getChildMenuList().add(m1_5);

        SysMenu m2 = new SysMenu("system", "系统管理", "layui-icon layui-icon-set", false, null);
        SysMenu m2_1 = new SysMenu("system", "用户管理", "layui-icon layui-icon-user", true, "/user/toUserList");
        SysMenu m2_2 = new SysMenu("system", "角色管理", "layui-icon layui-icon-group", true, "/role/toRoleList");
        SysMenu m2_3 = new SysMenu("system", "个人中心", "layui-icon layui-icon-username", true, "/user/toAdminProfile");
        m2.getChildMenuList().add(m2_1);
        m2.getChildMenuList().add(m2_2);
        m2.getChildMenuList().add(m2_3);

        ml.add(m1);
        ml.add(m2);
        return ml;
    }

    // 普通用户菜单（采购端）
    private List<SysMenu> getUserMenu() {
        List<SysMenu> ml = new ArrayList<>();
        SysMenu m2 = new SysMenu("system", "采购端", "layui-icon layui-icon-snowflake", false, null);
        SysMenu m2_0 = new SysMenu("system", "商品浏览", "layui-icon layui-icon-cart", true, "/product/toUserList");
        SysMenu m2_6 = new SysMenu("system", "采购计划", "layui-icon layui-icon-list", true, "/purchase/plan/list");
        SysMenu m2_7 = new SysMenu("system", "采购订单", "layui-icon layui-icon-file", true, "/purchaseOrder/toList");
        SysMenu m2_8 = new SysMenu("system", "供应商管理", "layui-icon layui-icon-group", true, "/supplier/toList");
        SysMenu m2_3 = new SysMenu("system", "个人中心", "layui-icon layui-icon-username", true, "/user/toProfile");
        m2.getChildMenuList().add(m2_0);
        m2.getChildMenuList().add(m2_6);
        m2.getChildMenuList().add(m2_7);
        m2.getChildMenuList().add(m2_8);
        m2.getChildMenuList().add(m2_3);
        ml.add(m2);
        return ml;
    }

    // 销售端菜单
    private List<SysMenu> getSalesMenu() {
        List<SysMenu> ml = new ArrayList<>();
        SysMenu m1 = new SysMenu("sales", "销售端", "layui-icon layui-icon-chart", false, null);
        SysMenu m1_1 = new SysMenu("sales", "客户管理", "layui-icon layui-icon-user", true, "/customer/toList");
        SysMenu m1_2 = new SysMenu("sales", "商品浏览", "layui-icon layui-icon-cart", true, "/product/toUserList");
        SysMenu m1_3 = new SysMenu("sales", "订单管理", "layui-icon layui-icon-file", true, "/orders/toList");
        SysMenu m1_4 = new SysMenu("sales", "个人中心", "layui-icon layui-icon-username", true, "/user/toProfile");
        SysMenu m1_5 = new SysMenu("sales", "地址管理", "layui-icon layui-icon-location", true, "/address/toList");
        m1.getChildMenuList().add(m1_1);
        m1.getChildMenuList().add(m1_2);
        m1.getChildMenuList().add(m1_3);
        m1.getChildMenuList().add(m1_4);
        m1.getChildMenuList().add(m1_5);
        ml.add(m1);
        return ml;
    }
}
