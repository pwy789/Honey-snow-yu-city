package com.pwy.enums;

import lombok.Getter;

@Getter
public enum VoucherStatusEnum {
    UNUSED("未使用",(short)0),
    Used("已使用",(short)1),
    EXPIRED("已过期",(short)2);
    private final String desc;

    private final Short code;

    VoucherStatusEnum(String desc,Short code){
        this.desc=desc;
        this.code=code;
    }
}
