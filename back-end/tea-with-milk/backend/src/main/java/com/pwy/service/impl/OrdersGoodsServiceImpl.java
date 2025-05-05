package com.pwy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.entity.pojo.OrdersGoods;
import com.pwy.mapper.OrdersGoodsMapper;
import com.pwy.service.OrdersGoodsService;
import org.springframework.stereotype.Service;

@Service
public class OrdersGoodsServiceImpl extends ServiceImpl<OrdersGoodsMapper, OrdersGoods> implements OrdersGoodsService {
}
