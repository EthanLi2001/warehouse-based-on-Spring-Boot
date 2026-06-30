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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_delivery_order")
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "delivery_id", type = IdType.AUTO)
    private Integer deliveryId;

    @TableField("deliveryNo")
    private String deliveryNo;

    @TableField("orderId")
    private Integer orderId;

    @TableField("logisticsCompany")
    private String logisticsCompany;

    @TableField("trackingNo")
    private String trackingNo;

    private String operator;

    private String remark;

    @TableField("createTime")
    private Date createTime;

    private Integer status;

    @TableField(exist = false)
    private String orderNo;

    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private String operatorName;
}
