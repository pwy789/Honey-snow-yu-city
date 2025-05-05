package com.pwy.entity.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Orders {
    private String id;

    private String userId;

    private String orderId;

    private String remark;

    private String address;

    private String name;

    private String phone;

    private Short gender;

    private LocalDateTime createTime;

    private Double totalPrice;

    private Short status;

    private String userVoucherId;

    private String shopId;

    private String shopName;

    private String deliveryFee;

    private String packagingFee;

    private LocalDateTime orderTime;

    private Short buyWay;
}
