package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaoye
 * @since 2025-04-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("t_order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "opid", type = IdType.AUTO)
    private Integer opid;

    private Integer oid;

    private Integer pid;

    private Integer quantity;

    private java.math.BigDecimal price;

    @TableField(exist = false)
    private String pname;

}
