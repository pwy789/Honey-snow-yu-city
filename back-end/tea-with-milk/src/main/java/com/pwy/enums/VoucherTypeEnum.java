package com.pwy.enums;

import lombok.Getter;


@Getter
public enum VoucherTypeEnum {
    DIRECT_DISCOUNT_DISCOUNT("直减型",(short)0),
    THRESHOLD_BASED_DISCOUNT("满减型",(short)1),
    DISCOUNT_RATE("折扣型",(short)2);
    private final String type;

    private final short code;

     VoucherTypeEnum(String type,short code){
        this.type=type;
        this.code=code;
    }
}
