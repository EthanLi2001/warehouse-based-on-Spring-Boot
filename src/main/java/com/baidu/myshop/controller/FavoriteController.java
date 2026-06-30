package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.Favorite;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.service.FavoriteService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import com.baidu.myshop.vo.FavoriteVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户收藏 前端控制器
 * </p>
 *
 * @author Gemini
 * @since 2025-05-31
 */
@Controller
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加到收藏
     */
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addFavorite(@RequestParam Integer productId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user2");
        if (currentUser == null) {
            return AjaxResult.error("用户未登录");
        }
        boolean success = favoriteService.addFavorite(currentUser.getUid(), productId);
        if (success) {
            return AjaxResult.right("收藏成功");
        } else {
            // 可能因为已经收藏过了，或者其他原因失败
            // isFavorite 已经处理了重复收藏，所以这里主要是其他错误
            return AjaxResult.error("收藏失败");
        }
    }

    /**
     * 取消收藏
     * @param productId 商品ID，通过商品ID和用户ID唯一确定一个收藏记录
     */
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult removeFavorite(@RequestParam Integer productId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user2");
        if (currentUser == null) {
            return AjaxResult.error("用户未登录");
        }
        boolean success = favoriteService.removeFavorite(currentUser.getUid(), productId);
        if (success) {
            return AjaxResult.right("取消收藏成功");
        } else {
            return AjaxResult.error("取消收藏失败");
        }
    }
    
    /**
     * 根据收藏ID取消收藏（用于收藏列表页直接删除）
     * @param favoriteId 收藏记录的ID
     */
    @PostMapping("/removeById")
    @ResponseBody
    public AjaxResult removeFavoriteById(@RequestParam Integer favoriteId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user2");
        if (currentUser == null) {
            return AjaxResult.error("用户未登录");
        }
        // 可选: 校验该favoriteId是否属于当前登录用户，防止越权删除
        Favorite fav = favoriteService.getById(favoriteId);
        if (fav == null || !fav.getUid().equals(currentUser.getUid())){
             return AjaxResult.error("操作失败或无权限");
        }
        boolean success = favoriteService.removeFavoriteById(favoriteId);
        if (success) {
            return AjaxResult.right("取消收藏成功");
        } else {
            return AjaxResult.error("取消收藏失败");
        }
    }

    /**
     * 检查商品是否已收藏
     */
    @GetMapping("/isFavorite")
    @ResponseBody
    public AjaxResult isFavorite(@RequestParam Integer productId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user2");
        if (currentUser == null) {
            return AjaxResult.error("用户未登录").put("isFavorite", false);
        }
        boolean isFavorite = favoriteService.isFavorite(currentUser.getUid(), productId);
        return AjaxResult.right().put("isFavorite", isFavorite);
    }

    /**
     * 跳转到我的收藏列表页面
     */
    @GetMapping("/myList")
    public String toMyFavorites() {
        return "favorite/list"; // 指向 templates/favorite/list.html
    }

    /**
     * 获取当前用户的收藏列表（分页）
     */
    @GetMapping("/getPage")
    @ResponseBody
    public PageResult<FavoriteVO> getMyFavoritesPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit, // 默认每页10条
            HttpSession session) {
        User currentUser = (User) session.getAttribute("user2");
        if (currentUser == null) {
            PageResult<FavoriteVO> emptyResult = new PageResult<>();
            emptyResult.setCode(1); 
            emptyResult.setMsg("用户未登录");
            emptyResult.setCount(0L);
            emptyResult.setData(java.util.Collections.emptyList());
            return emptyResult;
        }
        return favoriteService.getFavoritesByUserId(currentUser.getUid(), page, limit);
    }
} 