package com.baidu.myshop.controller;


import com.baidu.myshop.pojo.Role;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.service.RoleService;
import com.baidu.myshop.service.UserService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
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

@RequestMapping("/user")
public class UserController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @PostMapping("/batchDelUser")
    @ResponseBody
    public AjaxResult batchDeleteUsers(@RequestBody Map<String, List<Long>> params) {
        List<Long> userIds = params.get("userIds");
        if (userIds == null || userIds.isEmpty()) {
            return AjaxResult.error("请选择要删除的用户");
        }
        boolean success = userService.removeByIds(userIds);
        if (success) {
            return new AjaxResult();
        } else {
            return AjaxResult.error("批量删除失败");
        }
    }

    @RequestMapping ("/toUserList")
    public String toList(){
        return "user/list";
    }

    @RequestMapping("/toAddUser")
    public String toAddUser(Model model) {
        List<Role> roles = roleService.list(); // 获取所有角色
        model.addAttribute("roles", roles); // 将角色列表添加到模型中
        return "user/add";
    }

    @GetMapping("/toEditUser/{uid}")
    public String toEditUser(@PathVariable Integer uid, HttpServletRequest request ){
        User user = userService.getById(uid);
        List<Role> roles = roleService.list(); // 获取所有角色
        request.setAttribute("roles", roles); // 将角色列表添加到模型中
        request. setAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/editUser")
    @ResponseBody
    public AjaxResult editUser( User user) {
        boolean result = userService.updateById(user);
        if(result){
            return new AjaxResult();
        }else{
            return  AjaxResult.error();
        }
    }
    @PostMapping("/deleteUser/{uid}")
    @ResponseBody
    public AjaxResult deleteUser(@PathVariable Integer uid) {
        boolean success = userService.removeById(uid);
        if (success) {
            return new AjaxResult();
        } else {
            return AjaxResult.error("删除失败");
        }
    }
    @PostMapping("/addUser")
    @ResponseBody
    public AjaxResult addUser(User user) {
        try {
            boolean flag = userService.save(user);
            if (flag) {
                return AjaxResult.right("添加成功");
            } else {
                return AjaxResult.error("添加失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/registerUser")
    @ResponseBody
    public AjaxResult registerUser(User user, String confirmPassword) { //接收User对象和确认密码
        // 后端也需要校验两次密码是否一致，以及其他必要校验
        if (user.getPassword() == null || !user.getPassword().equals(confirmPassword)) {
            return AjaxResult.error("两次输入的密码不一致");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return AjaxResult.error("用户名不能为空");
        }
        // 简单校验密码长度，与前端一致
        if (user.getPassword().length() < 4) { 
            return AjaxResult.error("密码至少需要4位");
        }
        // 手机号校验，简单的正则表达式示例，具体需求可能更复杂
        if (user.getTelephone() != null && !String.valueOf(user.getTelephone()).matches("^1[3-9]\\d{9}$")){
            return AjaxResult.error("手机号格式不正确");
        }

        try {
            // 调用Service层进行用户注册
            boolean success = userService.registerUser(user);
            if (success) {
                return AjaxResult.right("注册成功！");
            } else {
                // Service层通常会通过异常来传递具体的错误原因，如用户名已存在
                return AjaxResult.error("注册失败，用户名可能已存在或系统错误。");
            }
        } catch (RuntimeException e) { 
            // 捕获Service层可能抛出的运行时异常（例如自定义的用户名已存在异常）
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            // 其他未知异常
            return AjaxResult.error("注册过程中发生异常，请稍后再试。");
        }
    }

    @PostMapping ("/getPage")
    @ResponseBody
    public PageResult<User> getPage(String pname, @RequestParam(defaultValue = "1")Integer page,
                                       @RequestParam(defaultValue = "5")  Integer limit){
        if ("".equals(pname)){
            pname=null;
        }
        PageResult<User> result= userService.getPage(pname,page,limit);
        return result;
    }

    @RequestMapping("/toLogin")
    public String toLogin(HttpServletRequest request){
        System.out.println("-----toLogin-----");
        List<Role> list=roleService.list();
        request.setAttribute("list",list);
        return "user/login";
    }

    @RequestMapping("/toResgister")
    public String toRegisterPage(Model model) {
        List<Role> roles = roleService.list(); // 获取所有角色
        // 一般来说，注册时不应允许选择所有角色，特别是某些特殊权限角色
        // 这里为了演示，加载所有角色。实际应用中可能需要过滤，例如只显示"普通用户"和"管理员"
        model.addAttribute("roles", roles);
        return "user/register";
    }

    @RequestMapping("/login")
    @ResponseBody
    public AjaxResult login(User user, HttpSession session){
        System.out.println("======= 登录请求接收 =======");
        System.out.println("接收到的用户对象:"+user);
        System.out.println("用户名: " + user.getName());
        System.out.println("密码: " + user.getPassword());
        System.out.println("角色ID: " + user.getRoleId());

        // 验证必填字段
        if(user.getName() == null || user.getName().trim().isEmpty()) {
            return AjaxResult.error("用户名不能为空");
        }
        if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return AjaxResult.error("密码不能为空");
        }
        if(user.getRoleId() == null) {
            return AjaxResult.error("请选择用户角色");
        }

        User user2=userService.login(user);
        System.out.println("查询到的用户对象:"+user2);
        
        if(user2!=null){
            session.setAttribute("user2",user2);
            //登录成功后保存用户的角色信息用于权限管理
            String roleName = "";
            if (user2.getRoleId() == 1) {
                session.setAttribute("role", "admin");
                roleName = "仓库管理员";
            } else if (user2.getRoleId() == 2) {
                session.setAttribute("role", "user");
                roleName = "采购专员";
            } else if (user2.getRoleId() == 3) {
                session.setAttribute("role", "sales");
                roleName = "销售专员";
            }
            System.out.println("登录成功，角色: " + roleName);
            return AjaxResult.right("登录成功，欢迎 " + user2.getName());
        }else {
            System.out.println("登录失败：用户名或密码或角色不匹配");
            return AjaxResult.error("登录失败：用户名或密码或角色不匹配，请检查后重试");
        }
    }
        @RequestMapping("/logout")
    public String logout(HttpSession session){
        System.out.println("-------logout---------");
        session.invalidate();
        return "redirect:/user/toLogin";
        }

    @GetMapping("/profile/info")
    @ResponseBody
    public AjaxResult getUserProfileInfo(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录或会话已过期，请重新登录。");
        }
        // 从数据库获取最新的用户信息，而不是直接用session中的（session中可能是旧的）
        User freshUserInfo = userService.getById(loggedInUser.getUid());
        if (freshUserInfo == null) {
            return AjaxResult.error("无法获取用户信息。");
        }
        // 出于安全考虑，不直接返回密码给前端，即使是当前用户
        freshUserInfo.setPassword(null); 
        return AjaxResult.right().put("user", freshUserInfo);
    }

    @PostMapping("/profile/update")
    @ResponseBody
    public AjaxResult updateUserProfile(@RequestBody User user, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录或会话已过期，请重新登录。");
        }
        // 确保用户只能修改自己的信息
        if (!loggedInUser.getUid().equals(user.getUid())) {
            return AjaxResult.error("非法操作：不允许修改他人信息。");
        }

        boolean success = userService.updateUserProfile(user);
        if (success) {
            // 如果信息更新成功，也更新一下 session 中的用户信息（特别是name, telephone）
            User updatedUserFromDb = userService.getById(user.getUid());
            if (updatedUserFromDb != null) {
                loggedInUser.setName(updatedUserFromDb.getName());
                loggedInUser.setTelephone(updatedUserFromDb.getTelephone());
                session.setAttribute("user2", loggedInUser);
            }
            return AjaxResult.right("用户信息更新成功！");
        } else {
            return AjaxResult.error("用户信息更新失败，请稍后再试。");
        }
    }

    @GetMapping("/toProfile")
    public String toUserProfilePage() {
        return "user/profile";
    }

    @GetMapping("/toAdminProfile")
    public String toAdminProfilePage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            // 如果未登录，重定向到登录页面
            return "redirect:/user/toLogin"; 
        }

        // 确保是管理员角色
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            // 如果不是管理员，但尝试访问此路径，可以重定向到错误页或其用户首页
            // 为简单起见，这里也重定向到登录页，或可以考虑抛出异常或返回特定错误视图
            return "redirect:/user/toLogin"; 
        }

        User freshUserInfo = userService.getById(loggedInUser.getUid());
        if (freshUserInfo == null) {
            // 理论上不应发生，除非用户在session中存在但在数据库中被删除
            return "redirect:/user/toLogin";
        }
        
        // 出于安全考虑，不直接将包含密码（即使是哈希）的对象完整传递到模型，除非页面确实需要它进行比对等操作
        // freshUserInfo.setPassword(null); // 如果profile页面不处理密码显示/修改，可以在这里置空

        model.addAttribute("adminUser", freshUserInfo);
        model.addAttribute("userRole", role); // 传递角色信息，页面可能需要
        return "admin/profile"; // 指向管理员的个人信息页面
    }
}
