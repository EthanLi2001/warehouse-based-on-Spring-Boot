package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("t_outbound_order")
public class OutboundOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "outbound_id", type = IdType.AUTO)
    private Integer outboundId;

    private String orderNo;

    private Integer type;

    private Integer orderId;

    private Integer productId;

    private Integer quantity;

    private String operator;

    private String remark;

    private Date createTime;

    private Integer status;

    @TableField(exist = false)
    private String operatorName;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String relatedOrderNo;
}