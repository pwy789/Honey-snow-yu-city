package com.pwy.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrdersVoList {
    private List<OrdersVo> ovs;
    private Long count;
}
