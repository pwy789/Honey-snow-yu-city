package com.pwy.entity.vo;

import lombok.Data;

@Data
public class DayCount {
    //哪一天
    private String day;
    //当天的订单数
    private Integer count;
}
