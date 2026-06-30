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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "customerId", type = IdType.AUTO)
    private Integer customerId;

    private String customerName;

    private String phoneNumber;

    private String contactPerson;

    private String detailedAddress;

    private Date createTime;

    private Date updateTime;
}