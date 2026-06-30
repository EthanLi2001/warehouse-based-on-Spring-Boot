package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.FavoriteMapper;
import com.baidu.myshop.dao.ProductMapper; // 需要引入ProductMapper来获取商品信息
import com.baidu.myshop.pojo.Favorite;
import com.baidu.myshop.pojo.Product; // 需要引入Product Pojo
import com.baidu.myshop.service.FavoriteService;
import com.baidu.myshop.utils.PageResult;
import com.baidu.myshop.vo.FavoriteVO; // 引入FavoriteVO
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList; // 引入 ArrayList
import java.util.List;
import java.util.Map; // 引入 Map
import java.util.stream.Collectors;

/**
 * <p>
 * 用户收藏表 服务实现类
 * </p>
 *
 * @author Gemini
 * @since 2025-05-31
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper; // 注入ProductMapper

    @Override
    @Transactional
    public boolean addFavorite(Integer userId, Integer productId) {
        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            return true; // 或者可以抛出异常，提示已收藏
        }
        Favorite favorite = new Favorite();
        favorite.setUid(userId);
        favorite.setPid(productId);
        favorite.setFavoriteTime(LocalDateTime.now());
        // favorite.setQuantity(1); // 如果需要设置默认数量
        return favoriteMapper.insert(favorite) > 0;
    }

    @Override
    @Transactional
    public boolean removeFavorite(Integer userId, Integer productId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId).eq("pid", productId);
        return favoriteMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean isFavorite(Integer userId, Integer productId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId).eq("pid", productId);
        return favoriteMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public PageResult<FavoriteVO> getFavoritesByUserId(Integer userId, int pageNum, int limit) { // 修改返回类型
        Page<Favorite> page = new Page<>(pageNum, limit);
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId).orderByDesc("favoriteTime");

        Page<Favorite> favoritePage = favoriteMapper.selectPage(page, queryWrapper);
        List<Favorite> favorites = favoritePage.getRecords();

        List<FavoriteVO> favoriteVOs = new ArrayList<>();
        if (favorites != null && !favorites.isEmpty()) {
            // 获取商品ID列表
            List<Integer> productIds = favorites.stream().map(Favorite::getPid).distinct().collect(Collectors.toList());

            if (!productIds.isEmpty()) {
                // 查询相关的商品信息
                List<Product> products = productMapper.selectBatchIds(productIds);
                // 将商品列表转换为 Map<pid, Product> 以便快速查找
                Map<Integer, Product> productMap = products.stream().collect(Collectors.toMap(Product::getPid, p -> p));

                for (Favorite fav : favorites) {
                    FavoriteVO vo = new FavoriteVO();
                    // 从 Favorite 实体复制属性
                    vo.setFavoriteId(fav.getFavoriteId());
                    vo.setUid(fav.getUid());
                    vo.setPid(fav.getPid());
                    vo.setFavoriteTime(fav.getFavoriteTime());
                    vo.setQuantity(fav.getQuantity());

                    // 从 Product 实体获取并设置属性
                    Product product = productMap.get(fav.getPid());
                    if (product != null) {
                        vo.setPname(product.getPname());
                        vo.setPrice(product.getPrice());
                        vo.setPfile(product.getPfile());
                    }
                    favoriteVOs.add(vo);
                }
            }
        }
        
        PageResult<FavoriteVO> pageResult = new PageResult<>(); // 修改泛型
        pageResult.setCode(0);
        pageResult.setMsg("查询收藏列表成功");
        pageResult.setCount(favoritePage.getTotal());
        pageResult.setData(favoriteVOs); // 设置 FavoriteVO 列表
        return pageResult;
    }
    
    @Override
    @Transactional
    public boolean removeFavoriteById(Integer favoriteId) {
        return favoriteMapper.deleteById(favoriteId) > 0;
    }
} 