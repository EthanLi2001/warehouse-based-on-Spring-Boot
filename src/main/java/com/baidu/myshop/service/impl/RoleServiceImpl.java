package com.baidu.myshop.service.impl;

import com.baidu.myshop.pojo.Role;
import com.baidu.myshop.dao.RoleMapper;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.service.RoleService;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaoye
 * @since 2025-04-15
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Autowired
    private RoleMapper roleMapper;
    public PageResult<Role> getPage(String pname, Integer currentPage, Integer pageSize) {
        Page<Role> page=new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        if (pname!=null){
            queryWrapper.like("roleName",pname);
        }
        List<Role> list=roleMapper.selectList(page,queryWrapper);
        Long count=roleMapper.selectCount(queryWrapper);
        PageResult<Role> pageResult=new PageResult<>();
        pageResult.setCode(0);
        pageResult.setMsg("分页查询商品信息成功");
        pageResult.setCount(count);
        pageResult.setData(list);
        return pageResult;
    }
}
