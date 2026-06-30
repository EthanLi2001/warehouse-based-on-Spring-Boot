package com.baidu.myshop.dao;

import com.baidu.myshop.pojo.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 购物车管理表 Mapper 接口
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

}
