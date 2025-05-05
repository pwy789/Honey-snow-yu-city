package com.pwy.entity.vo;

import lombok.Data;

@Data
public class OrdersGoodsVo {
    private String goodsName;

    private Integer price;

    private String coverImage;

    private Integer count;

    private String skuInfo;
}
