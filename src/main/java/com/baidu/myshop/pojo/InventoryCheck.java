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
@TableName("t_inventory_check")
public class InventoryCheck implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "check_id", type = IdType.AUTO)
    private Integer checkId;

    @TableField("checkNo")
    private String checkNo;

    @TableField("startTime")
    private Date startTime;

    @TableField("endTime")
    private Date endTime;

    private String operator;

    private String remark;

    private Integer status;
}