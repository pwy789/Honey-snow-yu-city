package com.pwy.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersVo {
    private String id;

    private String orderId;

    private String remark;

    private String address;

    private String name;

    private String phone;

    private Short gender;

    private LocalDateTime createTime;

    private Double totalPrice;

    private String deliveryFee;

    private String packagingFee;

    private LocalDateTime orderTime;

    private String voucherName;

    private Short status;

    private List<OrdersGoodsVo> ogv;

    private long total;
}
