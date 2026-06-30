package com.baidu.myshop.service.impl;

import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.dao.UserMapper;
import com.baidu.myshop.service.UserService;
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
public  class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(User user){
        System.out.println("========== 开始登录查询 ==========");
        System.out.println("传入的用户名: " + user.getName());
        System.out.println("传入的密码: " + user.getPassword());
        System.out.println("传入的角色ID: " + user.getRoleId());

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",user.getName());
        queryWrapper.eq("password",user.getPassword());
        queryWrapper.eq("roleId",user.getRoleId());

        System.out.println("执行SQL查询...");
        User user2=userMapper.selectOne(queryWrapper);

        System.out.println("查询结果: " + (user2 != null ? "找到用户" : "未找到用户"));
        if(user2 != null) {
            System.out.println("用户ID: " + user2.getUid());
            System.out.println("用户名: " + user2.getName());
            System.out.println("角色ID: " + user2.getRoleId());
        }
        System.out.println("========== 登录查询结束 ==========");

        return user2;
    }


    public PageResult<User> getPage(String pname, Integer currentPage, Integer pageSize) {
        Page<User> page=new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if (pname!=null){
            queryWrapper.like("name",pname);
        }
        List<User> list=userMapper.selectList(page,queryWrapper);
        Long count=userMapper.selectCount(queryWrapper);
        PageResult<User> pageResult=new PageResult<>();
        pageResult.setCode(0);
        pageResult.setMsg("分页查询商品信息成功");
        pageResult.setCount(count);
        pageResult.setData(list);
        return pageResult;
    }

    @Override
    public boolean updateUserProfile(User user) {
        if (user == null || user.getUid() == null) {
            return false; // 无效的用户ID
        }
        User existingUser = userMapper.selectById(user.getUid());
        if (existingUser == null) {
            return false; // 用户不存在
        }

        // 只更新允许用户修改的字段
        existingUser.setName(user.getName()); // 假设用户名可以修改
        existingUser.setTelephone(user.getTelephone());

        // 密码处理：如果传入的密码不为空，则更新密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            // 这里可以添加密码加密逻辑，例如使用Spring Security的PasswordEncoder
            // BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            existingUser.setPassword(user.getPassword()); // 暂时直接设置，实际项目中应加密
        }

        // roleId 和 createTime 通常不允许用户直接修改，所以这里不设置它们

        return userMapper.updateById(existingUser) > 0;
    }

    @Override
    public boolean registerUser(User user) {
        // 1. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", user.getName());
        if (this.count(queryWrapper) > 0) {
            // 在Controller层，我们会捕获这个异常并返回给用户
            throw new RuntimeException("用户名 '" + user.getName() + "' 已存在，请使用其他用户名。");
        }

        // 2. 密码加密 (重要：实际项目中必须加密密码)
        // String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        // user.setPassword(encodedPassword);
        // 目前暂不加密，如需加密，请取消注释并引入相关依赖 (e.g., spring-boot-starter-security)

        // 3. 设置默认角色为普通用户 (roleId = 2) -- 现在由前端传递，不再硬编码
        // 确保你的 t_role 表中 ID=2 的角色是普通用户
        // user.setRoleId(2); // 移除此行，roleId会从传递过来的user对象中获取

        // 4. 设置创建时间 (uid 会由MyBatis-Plus自动生成，如果主键策略是AUTO_INCREMENT)
        // user.setCreateTime(new java.util.Date()); // create_time 字段在 User Pojo 中可能没有，或者由数据库自动生成
        // 检查 User Pojo 是否有 createTime 字段并且需要程序设置
        // 如果数据库有默认值或触发器，可以不在此处设置
        // 假设 User Pojo 有 setCreateTime 方法
        if (user.getCreateTime() == null) { // 通常注册时创建时间是新的
             user.setCreateTime(new java.util.Date());
        }


        // 5. 保存用户
        boolean saved = this.save(user);
        if (!saved) {
            // 如果保存失败，但又不是因为用户名已存在（前面已检查），则可能是其他数据库问题
            throw new RuntimeException("注册用户失败，请联系管理员。");
        }
        return true;
    }
}
