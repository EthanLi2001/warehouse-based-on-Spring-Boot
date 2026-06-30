package com.baidu.myshop.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SysMenu {
    private String name;
    private String title;
    private String iconClass;
    private boolean child;
    private String href;
    private List<SysMenu> childMenuList;

    public SysMenu(){

    }
    public SysMenu(String name, String title, String iconClass, boolean child, String href) {
        this.name = name;
        this.title = title;
        this.iconClass = iconClass;
        this.child = child;
        this.href = href;
        this.childMenuList = new ArrayList<SysMenu>();
    }
}