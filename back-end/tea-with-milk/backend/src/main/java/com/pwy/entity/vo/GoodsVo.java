package com.pwy.entity.vo;

import com.pwy.entity.pojo.GoodsPictures;
import com.pwy.entity.pojo.GoodsSku;
import lombok.Data;

import java.util.List;


//响应给前端一件商品的完整数据
@Data
public class GoodsVo  {
 private String id;

 private String name;

 private String categoryId;

 private Integer price;

 private String introduction;

 private String description;

 private Integer balance;

 private Short status;

 private String coverImage;

 private List<GoodsPictures> images;

 private  List<GoodsSku> skus;


}
