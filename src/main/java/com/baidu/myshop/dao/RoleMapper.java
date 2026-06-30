package com.baidu.myshop.dao;

import com.baidu.myshop.pojo.Role;
import com.baidu.myshop.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaoye
 * @since 2025-04-15
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {


}
