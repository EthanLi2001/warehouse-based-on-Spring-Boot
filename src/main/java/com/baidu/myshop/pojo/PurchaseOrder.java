package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_purchase_order")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "poId", type = IdType.AUTO)
    private Integer poId;

    private String poNo;

    private Integer supplierId;

    private Integer productId;

    private Integer quantity;

    private BigDecimal price;

    private String remark;

    private Date createTime;

    private Integer status;

    private Integer planId;

    private String productName;

    private String supplierName;
}