package com.baidu.myshop.pojo.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductEditRequest {
    private Integer pid;
    private String pname;
    private BigDecimal price;
    private String pfile;
    private String description;
    private Integer minStock;
    private Integer maxStock;
}