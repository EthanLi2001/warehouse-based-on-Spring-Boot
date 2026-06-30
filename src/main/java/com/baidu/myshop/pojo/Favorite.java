package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户收藏表
 * </p>
 *
 * @author Gemini
 * @since 2025-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_favorite")
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Integer favoriteId;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 商品ID
     */
    private Integer pid;

    /**
     * 收藏时间
     */
    private LocalDateTime favoriteTime;

    /**
     * 数量 (如果收藏也与数量相关，否则可以考虑移除或忽略)
     */
    private Integer quantity;

    // 如果需要，可以添加一个 Product 对象用于联表查询时接收商品信息
    // @TableField(exist = false)
    // private Product product;

} 