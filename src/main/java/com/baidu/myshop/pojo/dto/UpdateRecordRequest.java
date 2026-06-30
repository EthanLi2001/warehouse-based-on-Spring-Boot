package com.baidu.myshop.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecordRequest {
    
    private Integer recordId;
    
    private Integer actualQty;
}