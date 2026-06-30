package com.baidu.myshop.pojo;

import java.math.BigDecimal;
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
@TableName("t_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pid", type = IdType.AUTO)
    private Integer pid;

    private String pname;

    private BigDecimal price;

    private String pfile;

    private String description;

    private Integer minStock;

    private Integer maxStock;
    
    @TableField(exist = false)
    private Integer quantity;
    
    @TableField(exist = false)
    private String warning;


}
