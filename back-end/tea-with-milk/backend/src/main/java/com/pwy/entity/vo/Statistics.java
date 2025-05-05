package com.pwy.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class Statistics {
    private List<OrdersStatistics> ordersStatistics;

    private List<GoodsStatistics> goodsStatistics;

    private List<VoucherStatistics> voucherStatistics;
    //每一条的订单量
    private List<DayCount>OrderCountByDay;


}
