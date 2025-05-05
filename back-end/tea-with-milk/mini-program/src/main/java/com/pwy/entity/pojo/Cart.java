package com.pwy.entity.pojo;

import lombok.Data;

@Data
public class Cart {
    private String id;

    private String userId;

    private String goodsId;

    private String skuInfo;

    private Integer count;

    private Integer price;
}
