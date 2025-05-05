package com.pwy.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
//维度SKU
@Data
public class Sku {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private  String name;

   //维度ID
    private  Integer dimensionId;


}
