package com.pwy.entity.pojo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Voucher {
    private String id;

    private String name;

    private String url;

    private Double discountRate;

    private Integer threshold;

    private Integer deduct;

    private LocalDateTime effectiveTime;

    private LocalDateTime expirationTime;

    private Integer number;

    private Short type;

    private Integer coinNeed;


}
