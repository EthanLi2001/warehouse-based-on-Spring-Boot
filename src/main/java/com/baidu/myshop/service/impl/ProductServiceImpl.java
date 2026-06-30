package com.baidu.myshop.service.impl;

import com.baidu.myshop.pojo.Inventory;
import com.baidu.myshop.pojo.Product;
import com.baidu.myshop.dao.ProductMapper;
import com.baidu.myshop.service.InventoryService;
import com.baidu.myshop.service.ProductService;
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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private InventoryService inventoryService;
    
    public PageResult<Product> getProductByPage(String pname, Integer currentPage, Integer pageSize) {
        Page<Product> page=new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        if (pname!=null){
            queryWrapper.like("pname",pname);
        }
        List<Product> list=productMapper.selectList(page,queryWrapper);
        
        for (Product product : list) {
            Inventory inventory = inventoryService.getByProductId(product.getPid());
            if (inventory != null) {
                product.setQuantity(inventory.getQuantity());
                product.setMinStock(inventory.getMinStock());
                product.setMaxStock(inventory.getMaxStock());
                
                if (inventory.getQuantity() < inventory.getMinStock()) {
                    product.setWarning("low");
                } else if (inventory.getQuantity() > inventory.getMaxStock()) {
                    product.setWarning("high");
                } else {
                    product.setWarning("normal");
                }
            } else {
                product.setQuantity(0);
                product.setMinStock(product.getMinStock() != null ? product.getMinStock() : 0);
                product.setMaxStock(product.getMaxStock() != null ? product.getMaxStock() : 0);
                product.setWarning("normal");
            }
        }
        
        Long count=productMapper.selectCount(queryWrapper);
        PageResult<Product> pageResult=new PageResult<>();
        pageResult.setCode(0);
        pageResult.setMsg("分页查询商品信息成功");
        pageResult.setCount(count);
        pageResult.setData(list);
        return pageResult;
    }
}
