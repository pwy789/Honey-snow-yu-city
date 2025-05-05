package com.pwy.entity.pojo;

import lombok.Data;

@Data
public class GoodsCategory {
    private String id;

    private String name;

    private Integer sort;

    private String image;

    private String description;

    private short status;
}
