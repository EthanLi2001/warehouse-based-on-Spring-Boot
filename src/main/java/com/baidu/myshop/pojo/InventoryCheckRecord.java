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

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("t_inventory_check_record")
public class InventoryCheckRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;

    @TableField("checkId")
    private Integer checkId;

    @TableField("productId")
    private Integer productId;

    @TableField("expectedQty")
    private Integer expectedQty;

    @TableField("actualQty")
    private Integer actualQty;

    @TableField("diffQty")
    private Integer diffQty;
}