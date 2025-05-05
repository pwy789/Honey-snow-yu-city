package com.pwy.entity.vo;

import lombok.Data;

@Data
public class CartVo {
    private String id;

    private String goodsId;

    private String skuInfo;

    private Integer count;

    private Integer price;

    private String coverImage;

    private String goodsName;
}
