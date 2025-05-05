package com.pwy.entity.pojo;

import lombok.Data;

@Data
public class Goods {
    private String id;

    private String name;
   //商品分类id
    private String categoryId;

    private Integer price;
    //商品简介
    private String introduction;

    private String description;
   //库存
    private Integer balance;
     //状态 0 停售 1起售
    private Short status;
    //封面图
    private String coverImage;
}
