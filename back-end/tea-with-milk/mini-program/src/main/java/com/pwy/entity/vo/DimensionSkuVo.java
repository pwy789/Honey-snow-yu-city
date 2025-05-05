package com.pwy.entity.vo;

import lombok.Data;
//维度和sku的关系
@Data
public class DimensionSkuVo {
    private Integer skuId;

    private Integer skuPrice;

    private String skuName;

    private Integer dimensionId;

    private String dimensionName;

    private Integer dimensionSort;
}
