package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.Address;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.service.AddressService;
import com.baidu.myshop.utils.AjaxResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 跳转到地址管理页面
    @GetMapping("/toList")
    public String toAddressListPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return "redirect:/user/toLogin"; // 用户未登录，重定向到登录页
        }
        // model.addAttribute("userId", loggedInUser.getUid()); // 可以不传，后端通过session获取
        return "user/address_list"; // 指向 templates/user/address_list.html
    }

    // 获取当前用户的所有地址
    @GetMapping("/list")
    @ResponseBody
    public AjaxResult getUserAddresses(HttpSession session, @RequestParam(required = false) String keyword) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录");
        }
        List<Address> addresses = addressService.getAddressesByUserId(loggedInUser.getUid(), keyword);
        return AjaxResult.right().put("data", addresses);
    }

    // 添加地址
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addAddress(@RequestBody Address address, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录");
        }
        address.setUid(loggedInUser.getUid());
        if (addressService.addAddress(address)) {
            return AjaxResult.right("地址添加成功");
        } else {
            return AjaxResult.error("地址添加失败");
        }
    }

    // 更新地址
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult updateAddress(@RequestBody Address address, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录");
        }
        // 确保用户只能修改自己的地址
        Address existingAddress = addressService.getById(address.getAddressId());
        if (existingAddress == null || !existingAddress.getUid().equals(loggedInUser.getUid())){
            return AjaxResult.error("非法操作或地址不存在");
        }
        address.setUid(loggedInUser.getUid()); // 再次确认UID
        if (addressService.updateAddress(address)) {
            return AjaxResult.right("地址更新成功");
        } else {
            return AjaxResult.error("地址更新失败");
        }
    }

    // 删除地址
    @PostMapping("/delete/{addressId}")
    @ResponseBody
    public AjaxResult deleteAddress(@PathVariable Integer addressId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录");
        }
        if (addressService.deleteAddress(loggedInUser.getUid(), addressId)) {
            return AjaxResult.right("地址删除成功");
        } else {
            return AjaxResult.error("地址删除失败或无权操作");
        }
    }

    // 设置默认地址
    @PostMapping("/setDefault/{addressId}")
    @ResponseBody
    public AjaxResult setDefaultAddress(@PathVariable Integer addressId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录");
        }
        if (addressService.setDefaultAddress(loggedInUser.getUid(), addressId)) {
            return AjaxResult.right("默认地址设置成功");
        } else {
            return AjaxResult.error("默认地址设置失败");
        }
    }
    
    // 根据ID获取地址信息（用于编辑时回显）
    @GetMapping("/get/{addressId}")
    @ResponseBody
    public AjaxResult getAddressById(@PathVariable Integer addressId, HttpSession session){
        User loggedInUser = (User) session.getAttribute("user2");
        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录");
        }
        Address address = addressService.getById(addressId);
        if(address == null || !address.getUid().equals(loggedInUser.getUid())){
            return AjaxResult.error("地址不存在或无权查看");
        }
        return AjaxResult.right().put("address", address);
    }
} 