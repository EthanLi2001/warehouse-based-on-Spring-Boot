package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Cart;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 购物车管理表 服务类
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
public interface CartService extends IService<Cart> {

    /**
     * 查询所有购物车信息
     * @param cid 购物车ID,用于模糊查询
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    PageResult<Cart> getAllCart(String cid, Integer currentPage, Integer pageSize);

    /**
     * 批量删除
     * @param cartIds
     * @return
     */
    int deleteCartByIds(Integer[] cartIds);

    /**
     * 根据id删除
     * @param cid
     * @return
     */
    int deleteCartById(Integer cid);

    /**
     * 新增购物车
     * @param cart
     * @return
     */
    int insertCart(Cart cart);

    /**
     * 根据id查询
     * @param cid 购物车ID
     * @return
     */
    Cart getCartById(Integer cid);

    /**
     * 修改购物车
     * @param cart
     * @return
     */
    int updateCart(Cart cart);

    /**
     * 查询当前登录用户的所有购物车信息
     * @param userId 当前登录用户的ID
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    PageResult<Cart> getCurrentUserCart(Integer userId, Integer currentPage, Integer pageSize);

    /**
     * 添加商品到购物车，如果已存在则更新数量
     * @param cart 包含 uid, pid, quantity 的购物车项
     * @return 操作是否成功
     */
    boolean addItemToCart(Cart cart);

}
