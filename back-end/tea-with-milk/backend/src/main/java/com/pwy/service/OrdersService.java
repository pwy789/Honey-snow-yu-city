package com.pwy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pwy.entity.dto.SearchDto;
import com.pwy.entity.pojo.Orders;
import com.pwy.entity.vo.OrdersVo;
import com.pwy.entity.vo.PageOrdersVo;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    OrdersVo getOrdersById(String id);

    List<OrdersVo> getOrderByStatus(Short status);

   PageOrdersVo searchOrderByCondition(SearchDto dto);

    void finishOrder(String id);
}
