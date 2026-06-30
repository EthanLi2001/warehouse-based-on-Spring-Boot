package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_purchase_plan")
public class PurchasePlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "planId", type = IdType.AUTO)
    private Integer planId;

    private String planNo;

    private Integer productId;

    private Integer quantity;

    private String reason;

    private Date createTime;

    private Integer status;

    private String remark;

    private Date updateTime;

    private String planName;

    private String productName;

    private String supplierName;
}