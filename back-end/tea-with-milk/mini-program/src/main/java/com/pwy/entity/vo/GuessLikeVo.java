package com.pwy.entity.vo;

import lombok.Data;
//用于返回猜你喜欢数据
@Data
public class GuessLikeVo {
    private String id;

    private String name;

    private String coverImage;

    private Integer price;
}
