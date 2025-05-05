package com.pwy.entity.dto;

import lombok.Data;



@Data
public class SearchDto {
    private String orderNum;

    private String beginTime;

    private String endTime;

    private String status;

    private Integer page;

    private Integer pageSize;
}
