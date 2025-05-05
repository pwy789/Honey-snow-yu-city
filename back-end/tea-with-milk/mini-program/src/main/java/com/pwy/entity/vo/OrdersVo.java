package com.pwy.entity.vo;


import com.pwy.entity.pojo.Orders;
import com.pwy.entity.pojo.OrdersGoods;
import com.pwy.entity.pojo.Voucher;
import lombok.Data;

import java.util.List;

@Data
public class OrdersVo  {
    private Orders orders;

    private List<OrdersGoods> ogs;

    private Voucher voucher;


}
