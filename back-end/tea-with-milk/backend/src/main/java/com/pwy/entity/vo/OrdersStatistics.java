package com.pwy.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersStatistics {
    //订单类型 自提或者外送
    private Short orderWay;

    private BigDecimal orderTotalPrice;

    private Integer orderCount;

}
