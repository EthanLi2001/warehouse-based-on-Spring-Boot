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
@TableName("t_inventory")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "inventory_id", type = IdType.AUTO)
    private Integer inventoryId;

    @TableField("productId")
    private Integer productId;

    private Integer quantity;

    @TableField("minStock")
    private Integer minStock;

    @TableField("maxStock")
    private Integer maxStock;

    @TableField("batchNo")
    private String batchNo;

    @TableField("createTime")
    private Date createTime;

    @TableField("updateTime")
    private Date updateTime;
}