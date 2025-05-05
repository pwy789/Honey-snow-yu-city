package com.pwy.utils;

import lombok.Data;

import java.time.LocalDateTime;

//此类用于判断订单是否超时
@Data
public class OrderIsTimeoutUtils {
    private String orderId;

    private LocalDateTime createTime;
}
