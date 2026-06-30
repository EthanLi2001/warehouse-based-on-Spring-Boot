package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Role;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaoye
 * @since 2025-04-15
 */
public interface RoleService extends IService<Role> {

    PageResult<Role> getPage(String pname, Integer page, Integer limit);
}
