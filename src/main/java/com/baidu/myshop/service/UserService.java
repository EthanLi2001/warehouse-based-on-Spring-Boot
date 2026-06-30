package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Product;
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
public interface UserService extends IService<User> {

    public User login(User user);

    PageResult<User> getPage(String pname, Integer page, Integer limit);

    /**
     * 更新用户个人信息
     * @param user 包含要更新信息的用户对象
     * @return 是否更新成功
     */
    boolean updateUserProfile(User user);

    boolean registerUser(User user);
}
