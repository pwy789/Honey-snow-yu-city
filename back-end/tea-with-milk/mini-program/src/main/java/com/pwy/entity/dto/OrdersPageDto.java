package com.pwy.entity.dto;

import lombok.Data;

import java.util.List;

//分页获取不同状态的订单数据
@Data
public class OrdersPageDto {
    private Integer page;

    private Integer pageSize;

    private List<Integer> status;
}
