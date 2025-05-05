package com.pwy.entity.dto;

import lombok.Data;
//前端传给后端用于判断是要进行增删的哪一个操作
@Data
public class SkuOperate {
    private Integer skuId;

    private Integer price;

    private String operate;

}
