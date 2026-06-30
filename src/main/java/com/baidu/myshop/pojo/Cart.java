package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 购物车管理表
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cart")
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车ID
     */
    @TableId(value = "cid", type = IdType.AUTO)
    private Integer cid;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 商品ID
     */
    private Integer pid;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 加入时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date addedTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String prodName;

    @TableField(exist = false)
    private String pfile;

    @TableField(exist = false)
    private Double price;

    @TableField(exist = false)
    private Double subtotal;

}
