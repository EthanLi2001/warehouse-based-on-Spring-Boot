package com.baidu.myshop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "addressId", type = IdType.AUTO)
    private Integer addressId;

    private Integer uid;

    private String consigneeName;

    private String phoneNumber;

    private String province;

    private String city;

    private String district;

    private String detailedAddress;

    private Integer isDefault;

    private Date createTime;

    private Date updateTime;
} 