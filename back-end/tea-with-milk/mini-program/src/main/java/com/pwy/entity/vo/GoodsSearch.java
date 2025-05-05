package com.pwy.entity.vo;

import lombok.Data;
//搜索时返回的商品数据
@Data
public class GoodsSearch {

    private String id;

    private String name;

    private Integer price;

    private String coverImage;

    private String introduction;

}
