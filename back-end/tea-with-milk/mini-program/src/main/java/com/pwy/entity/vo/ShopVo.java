package com.pwy.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopVo {
    private String id;

    private String name;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String position;

    private String phone;
}
