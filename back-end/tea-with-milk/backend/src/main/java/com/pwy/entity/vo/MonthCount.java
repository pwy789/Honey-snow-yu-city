package com.pwy.entity.vo;

import lombok.Data;

@Data
public class MonthCount {
    //哪一月
    private String month;

    //这一月的订单数量
    private Integer count;
}
