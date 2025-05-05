package com.pwy.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//商品包含的图片
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsPictures {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String goodsId;
   //图片URL
    private String url;
}
