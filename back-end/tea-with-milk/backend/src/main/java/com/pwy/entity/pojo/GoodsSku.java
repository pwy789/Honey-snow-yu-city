package com.pwy.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//商品包含的sku
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSku {
    private Integer id;

    private String goodsId;

    private Integer skuId;

    private Integer price;
}
