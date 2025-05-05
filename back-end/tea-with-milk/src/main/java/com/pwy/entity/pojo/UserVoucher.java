package com.pwy.entity.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVoucher {
    private String id;

    private String userId;

    private String voucherId;

    private Short status;
    //获取时间
    private LocalDateTime acquireTime;
    //有效天数
    private Integer validityPeriod;
    //优惠券使用时间
    private LocalDateTime useTime;
}
