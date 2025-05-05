package com.pwy.entity.vo;

import com.pwy.entity.pojo.Voucher;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoucherVo {
    private Voucher voucher;

    private Short status;

     private String id;
    //获取时间
    private LocalDateTime acquireTime;
    //有效天数
    private Integer validityPeriod;


}
