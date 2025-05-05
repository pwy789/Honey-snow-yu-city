package com.pwy.entity.dto;

import lombok.Data;

@Data
public class CartDto {
    private String id;

    private String userId;

    private String goodsId;

    private String skuInfo;

    private Integer count;

    private Integer price;

    private String coverImage;
    //商品名称
    private String goodsName;


}
