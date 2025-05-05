package com.pwy.enums;

import lombok.Getter;

@Getter
public enum OrderBuyWayEnums {
    PICK_UP("自提",(short)0),
    DELIVERY("外送",(short)1);


    private final String buyWay;

    private final Short code;

    OrderBuyWayEnums(String buyWay,Short code){
        this.buyWay=buyWay;
        this.code=code;
    }
}
