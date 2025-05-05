package com.pwy.enums;


import lombok.Getter;

@Getter
public enum OrdersStatusEnums {
    PENDING_PAYMENT("待支付",(short)0),
    ALREADY_CANCEL("已取消",(short)1),
    IN_THE_MAKING("制作中",(short)2),
    OUT_OF_DELIVERY("配送中",(short)3),
    COMPLETED("已完成",(short)4),
    SUCCESSFULLY_DELIVERED("已送达",(short)5);


    private final String status;

    private final Short code;

    OrdersStatusEnums(String status, Short code){
        this.status=status;
        this.code=code;
    }
}
