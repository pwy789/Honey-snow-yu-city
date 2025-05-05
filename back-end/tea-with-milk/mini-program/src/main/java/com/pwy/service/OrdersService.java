package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.common.Result;
import com.pwy.entity.dto.CartDto;
import com.pwy.entity.dto.FeeDto;
import com.pwy.entity.dto.OrderDto;
import com.pwy.entity.dto.OrdersPageDto;
import com.pwy.entity.pojo.Orders;
import com.pwy.entity.vo.FeeVo;
import com.pwy.entity.vo.OrdersVo;
import com.pwy.entity.vo.OrdersVoList;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    Result Order(OrderDto dto);

    OrdersVo getOrderById(String id);

    void setOrderStatus(String orderId,Short status);

    OrdersVoList getOrdersByStatus(OrdersPageDto pageDto);

    void confirmPay(String id);

    FeeVo createFee(FeeDto feeDto);

    Result oneMoreOrder(String orderId);

    String deduceBalance(OrderDto dto, List<CartDto> goods);
}
