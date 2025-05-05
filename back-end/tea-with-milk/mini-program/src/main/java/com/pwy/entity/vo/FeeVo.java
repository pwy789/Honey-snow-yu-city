package com.pwy.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeVo {
    private double deliveryFee;

    private BigDecimal packagingFee;
}
