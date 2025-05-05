package com.pwy.entity.dto;

import lombok.Data;
//用于接收商品查询的条件
@Data
public class GoodsSearchDto {
    private String name;

    private String categoryId;

    private Integer priceLow;

    private Integer priceHigh;

    private Short status;

    private Integer page;

    private Integer pageSize;
}
