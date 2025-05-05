package com.pwy.entity.vo;

import com.pwy.entity.pojo.Sku;
import lombok.Data;

import java.util.List;

//这个vo用户返回维度及其sku
@Data
public class DimensionVo {
    private Integer id;

    private String name;

    private Integer sort;

    private List<Sku> skus;
}
