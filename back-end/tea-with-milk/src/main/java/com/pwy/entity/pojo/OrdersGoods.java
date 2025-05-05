package com.pwy.entity.pojo;
import lombok.Data;

@Data
public class OrdersGoods {
    private String id;

    private String ordersId;

    private String goodsId;

    private String cartId;

    private String skuInfo;

    private Integer count;

    private Integer price;

    private String coverImage;

    private String goodsName;



}
