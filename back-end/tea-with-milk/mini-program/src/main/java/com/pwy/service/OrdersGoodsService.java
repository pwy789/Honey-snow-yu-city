package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.pojo.OrdersGoods;

import java.util.List;

public interface OrdersGoodsService extends IService<OrdersGoods> {
    void saveOrdersGoods(List<OrdersGoods> ogs);

    //根据订单表的id恢复库存
    void restoreBalanceById(String id);

    //查询出某订单下所包含的商品
    List<OrdersGoods> getOrdersGoodsByOrderId(String id);
}
