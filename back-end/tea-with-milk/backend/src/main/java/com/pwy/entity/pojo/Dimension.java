package com.pwy.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

//商品维度
@Data
public class Dimension {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer sort;
}
