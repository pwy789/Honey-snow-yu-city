package com.pwy.entity.dto;

import lombok.Data;
//购买方式为外送时需要产生配送费以及打包费
@Data
public class FeeDto {

    //店铺id
    private String shopId;
    //收货地址id
    private String addressId;
    //商品个数
    private Integer shopNum;
}
