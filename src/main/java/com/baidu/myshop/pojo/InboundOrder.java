package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("t_inbound_order")
public class InboundOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "inbound_id", type = IdType.AUTO)
    private Integer inboundId;

    private String orderNo;

    private Integer type;

    private Integer supplierId;

    private Integer productId;

    private Integer quantity;

    private String batchNo;

    private String operator;

    private String remark;

    private Date createTime;

    private Integer status;
}