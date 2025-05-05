package com.pwy.entity.dto;

import lombok.Data;
//前端传给后端用于判断是要进行增删改的哪一个操作
@Data
public class ImageOperate {
    private Integer id;

    private String url;

    private String operation;
}
