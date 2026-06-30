package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Orders;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单管理表 服务类
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 查询所有订单信息
     * @param oid 订单ID,用于模糊查询
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    PageResult<Orders> getAllOrders(String oid, Integer currentPage, Integer pageSize);

    /**
     * 批量删除
     * @param orderIds
     * @return
     */
    int deleteOrdersByIds(Integer[] orderIds);

    /**
     * 根据id删除
     * @param oid
     * @return
     */
    int deleteOrderById(Integer oid);

    /**
     * 新增订单
     * @param orders
     * @return
     */
    int insertOrder(Orders orders);

    /**
     * 根据id查询
     * @param oid 订单ID
     * @return
     */
    Orders getOrderByOid(Integer oid);

    /**
     * 修改订单
     * @param orders
     * @return
     */
    int updateOrder(Orders orders);

    /**
     * 从购物车创建订单
     * @param userId 用户ID
     * @param cartItemIds 选中的购物车项ID列表
     * @return 创建的订单对象或包含订单ID的结果
     * @throws Exception 如果创建过程中发生错误（如商品无效、库存不足等）
     */
    Orders createOrderFromCart(Integer userId, List<Integer> cartItemIds) throws Exception;

    /**
     * 获取最后插入的订单ID
     * @return
     */
    Integer getLastOrderId();

}
