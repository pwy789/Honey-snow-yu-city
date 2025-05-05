package com.pwy.controller;

import com.pwy.common.Result;
import com.pwy.entity.dto.SearchDto;
import com.pwy.entity.vo.OrdersVo;
import com.pwy.entity.vo.PageOrdersVo;
import com.pwy.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    //根据订单的主键id查询订单
   @GetMapping("/{id}")
    public Result getOrdersById(@PathVariable String id){
       OrdersVo ov = ordersService.getOrdersById(id);
       return Result.success(ov);
   }
   //查询订单
    @GetMapping("/list")
    public Result getOrderList(Short status){
        List<OrdersVo> ov = ordersService.getOrderByStatus(status);
        return  Result.success(ov);
    }
    @PostMapping("/search")
    //按条件搜寻订单
    public Result searchOrderByCondition(@RequestBody SearchDto dto){
       log.info("dto: {}",dto);
        PageOrdersVo pov = ordersService.searchOrderByCondition(dto);
        return Result.success(pov);
    }
    //完成订单
    @PostMapping("/finish/{id}")
    public Result finishOrder(@PathVariable String id){
       ordersService.finishOrder(id);
       return Result.success();
    }
}
