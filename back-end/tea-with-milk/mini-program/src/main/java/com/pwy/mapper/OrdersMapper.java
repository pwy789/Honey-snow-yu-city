package com.pwy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pwy.entity.pojo.Orders;
import com.pwy.entity.pojo.OrdersGoods;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    List<OrdersGoods> getOrderGoodsById(String id);
}
