package com.pwy.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private List<CartDto> goods;

    private Double totalPrice;

    private String address;

    //联系人姓名
    private String name;

    private String phone;

    private Short gender;
    //备注
    private String remark;

    private String shopId;

    private String shopName;

    private String deliveryFee;

    private String packagingFee;

    private String voucherId;
}
