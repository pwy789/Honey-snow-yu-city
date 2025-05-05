package com.pwy.entity.vo;

import lombok.Data;

import java.util.List;

//用来响应给前端的维度-sku数据
@Data
public class DimensionSkuResult {
    //维度id
    private Integer dimensionId;

    //维度名称
    private String dimensionName;
    //该维度包含的sku信息
    private List<DimensionSkuVo> skus;
}
