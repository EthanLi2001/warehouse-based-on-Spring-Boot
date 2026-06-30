package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.CartMapper;
import com.baidu.myshop.dao.ProductMapper;
import com.baidu.myshop.dao.UserMapper;
import com.baidu.myshop.pojo.Cart;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.service.CartService;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 购物车管理表 服务实现类
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询所有购物车信息
     *
     * @param cid 购物车ID,用于模糊查询
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public PageResult<Cart> getAllCart(String cid, Integer currentPage, Integer pageSize) {
        Page<Cart> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        if (cid != null && !cid.trim().isEmpty()) {
            wrapper.like("cid", cid);
        }
        List<Cart> cartItems = cartMapper.selectList(page, wrapper);
        
        cartItems.forEach(item -> {
            Product product = productMapper.selectById(item.getPid());
            if (product != null) {
                item.setProdName(product.getPname());
                item.setPfile(product.getPfile());
                if (product.getPrice() != null) {
                    item.setPrice(product.getPrice().doubleValue());
                    if (item.getQuantity() != null) {
                        item.setSubtotal(item.getPrice() * item.getQuantity());
                    }
                }
            }
            if (item.getUid() != null) {
                item.setUserName(userMapper.selectById(item.getUid()).getName());
            }
        });
        Long l = cartMapper.selectCount(wrapper);
        PageResult<Cart> cartPageResult = new PageResult<>();
        cartPageResult.setCode(0);
        cartPageResult.setData(cartItems);
        cartPageResult.setMsg("分页成功");
        cartPageResult.setCount(l);

        return cartPageResult;
    }

    /**
     * 批量删除
     *
     * @param cartIds
     * @return
     */
    @Override
    public int deleteCartByIds(Integer[] cartIds) {
        return cartMapper.deleteBatchIds(Arrays.asList(cartIds));
    }

    /**
     * 根据id删除
     *
     * @param cid
     * @return
     */
    @Override
    public int deleteCartById(Integer cid) {
        return cartMapper.deleteById(cid);
    }

    /**
     * 新增购物车
     *
     * @param cart
     * @return
     */
    @Override
    public int insertCart(Cart cart) {
        cart.setAddedTime(new Date());
        return cartMapper.insert(cart);
    }

    /**
     * 根据id查询
     *
     * @param cid
     * @return
     */
    @Override
    public Cart getCartById(Integer cid) {
        return cartMapper.selectById(cid);
    }

    /**
     * 修改购物车
     *
     * @param cart
     * @return
     */
    @Override
    public int updateCart(Cart cartDetailsToUpdate) { 
        if (cartDetailsToUpdate.getCid() == null || cartDetailsToUpdate.getQuantity() == null || cartDetailsToUpdate.getQuantity() < 1) {
            return 0; // Invalid input
        }

        // 可选：进行权限校验，确保当前用户只能修改自己的购物车项
        // 例如: User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Cart existingCart = cartMapper.selectById(cartDetailsToUpdate.getCid());
        // if(existingCart == null || !existingCart.getUid().equals(currentUser.getUid())){
        //     throw new AccessDeniedException("无权修改此购物车项");
        // }

        LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Cart::getCid, cartDetailsToUpdate.getCid())
                     .set(Cart::getQuantity, cartDetailsToUpdate.getQuantity());
        // 当使用LambdaUpdateWrapper并调用set方法时，第一个参数实体可以为null
        return cartMapper.update(null, updateWrapper); 
    }

    @Override
    public PageResult<Cart> getCurrentUserCart(Integer userId, Integer currentPage, Integer pageSize) {
        Page<Cart> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        
        if (userId == null) {
            // 如果没有提供用户ID（例如未登录或意外情况），返回空结果或抛出异常
            // 这里为了安全，返回空的分页结果
            PageResult<Cart> emptyResult = new PageResult<>();
            emptyResult.setCode(1); // 表示错误或无数据
            emptyResult.setMsg("用户未登录或用户ID无效");
            emptyResult.setCount(0L);
            emptyResult.setData(java.util.Collections.emptyList());
            return emptyResult;
        }
        
        wrapper.eq("uid", userId); // 根据用户ID查询
        wrapper.orderByDesc("addedTime"); // 按加入时间降序排序，最新的在前面

        List<Cart> cartItems = cartMapper.selectList(page, wrapper);

        cartItems.forEach(item -> {
            Product product = productMapper.selectById(item.getPid());
            if (product != null) {
                item.setProdName(product.getPname());
                item.setPfile(product.getPfile());
                if (product.getPrice() != null) {
                    item.setPrice(product.getPrice().doubleValue());
                    if (item.getQuantity() != null) {
                        item.setSubtotal(item.getPrice() * item.getQuantity());
                    }
                }
            }
            // 用户名不需要再查了，因为这已经是特定用户的购物车
            // if (item.getUid() != null) { 
            //    User user = userMapper.selectById(item.getUid());
            //    if(user != null) item.setUserName(user.getName());
            // }
        });

        Long totalCount = cartMapper.selectCount(wrapper);
        
        PageResult<Cart> cartPageResult = new PageResult<>();
        cartPageResult.setCode(0);
        cartPageResult.setData(cartItems);
        cartPageResult.setMsg("查询成功");
        cartPageResult.setCount(totalCount);

        return cartPageResult;
    }

    @Override
    public boolean addItemToCart(Cart cart) {
        if (cart.getUid() == null || cart.getPid() == null || cart.getQuantity() == null) {
            throw new IllegalArgumentException("用户ID, 商品ID 和数量不能为空。");
        }
        if (cart.getQuantity() <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0。");
        }

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", cart.getUid());
        queryWrapper.eq("pid", cart.getPid());
        Cart existingCartItem = this.getOne(queryWrapper); // Use this.getOne for ServiceImpl

        if (existingCartItem != null) {
            // Item exists, update quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cart.getQuantity());
            existingCartItem.setAddedTime(new Date()); // Update added time as well
            return this.updateById(existingCartItem); // Use this.updateById for ServiceImpl
        } else {
            // Item does not exist, insert new
            cart.setAddedTime(new Date());
            return this.save(cart); // Use this.save for ServiceImpl
        }
    }

}
