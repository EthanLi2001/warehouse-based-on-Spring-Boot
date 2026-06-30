package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Product;
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
public interface ProductService extends IService<Product> {
    public PageResult<Product> getProductByPage(String pname, Integer page, Integer pageSize);

}
