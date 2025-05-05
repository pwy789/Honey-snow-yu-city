package com.pwy.entity.dto;


import com.pwy.entity.pojo.GoodsPictures;
import com.pwy.entity.pojo.GoodsSku;
import lombok.Data;

import java.util.List;

//用于接收前端传来的奶茶信息
@Data
public class GoodsInfoDto {
    private String id;

    private String name;

    private String categoryId;

    private Integer price;

    private String introduction;

    private String description;

    private Integer balance;

    private Short status;
   //接收图片数组
    private List<GoodsPictures> images;

    private List<ImageOperate> imageOperate;

    private List<SkuOperate> skuOperate;
}
