package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Favorite;
import com.baidu.myshop.utils.PageResult;
import com.baidu.myshop.vo.FavoriteVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户收藏表 服务类
 * </p>
 *
 * @author Gemini
 * @since 2025-05-31
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 添加商品到收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否添加成功
     */
    boolean addFavorite(Integer userId, Integer productId);

    /**
     * 从收藏中移除商品
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否移除成功
     */
    boolean removeFavorite(Integer userId, Integer productId);

    /**
     * 检查商品是否已被用户收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return true 如果已收藏，false 如果未收藏
     */
    boolean isFavorite(Integer userId, Integer productId);

    /**
     * 分页获取用户的收藏列表 (包含商品详细信息)
     *
     * @param userId 用户ID
     * @param page 当前页码
     * @param limit 每页数量
     * @return 分页的收藏结果，其中Favorite对象可能包含Product信息
     */
    PageResult<FavoriteVO> getFavoritesByUserId(Integer userId, int page, int limit);
    
    /**
     * 根据收藏ID删除收藏记录
     * @param favoriteId 收藏ID
     * @return 是否删除成功
     */
    boolean removeFavoriteById(Integer favoriteId);

} 