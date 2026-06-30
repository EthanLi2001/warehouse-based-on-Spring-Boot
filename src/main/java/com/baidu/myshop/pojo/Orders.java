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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单管理表
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
@AllArgsConstructor
@NoArgsConstructor
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "oid", type = IdType.AUTO)
    private Integer oid;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 订单状态（0: 待付款, 1: 待制作, 2: 待配送, 3: 已完成, 4: 已取消）
     */
    private Integer status;

    /**
     * 订单号（XS开头）
     */
    private String orderNo;

    /**
     * 订单总价
     */
    private BigDecimal totalPrice;

    /**
     * 下单时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date orderTime;

    /**
     * 配送时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date deliveryTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String productNames;

    @TableField(exist = false)
    private String deliveryNo;

    @TableField(exist = false)
    private String outboundNo;


}
