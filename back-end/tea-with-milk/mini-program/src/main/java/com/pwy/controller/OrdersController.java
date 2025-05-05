package com.pwy.controller;

import cn.hutool.json.JSONUtil;
import com.pwy.common.RedisKeyConstant;
import com.pwy.common.Result;
import com.pwy.entity.dto.FeeDto;
import com.pwy.entity.dto.OrderDto;
import com.pwy.entity.dto.OrdersPageDto;
import com.pwy.entity.vo.FeeVo;
import com.pwy.entity.vo.OrdersVo;
import com.pwy.entity.vo.OrdersVoList;
import com.pwy.enums.OrdersStatusEnums;
import com.pwy.service.OrdersGoodsService;
import com.pwy.service.OrdersService;
import com.pwy.utils.OrderIsTimeoutUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersGoodsService ordersGoodsService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/createorder")
    public Result Order(@RequestBody OrderDto dto){


        return ordersService.Order(dto);
    }
    @GetMapping("/{id}")
    public Result getOrderById(@PathVariable String id){
        OrdersVo ov = ordersService.getOrderById(id);

        return Result.success(ov);
    }
    //生成订单小费
    @PostMapping("/fee")
    public Result createFee(@RequestBody FeeDto feeDto){

        FeeVo fee = ordersService.createFee(feeDto);
        return  Result.success(fee);
    }
    //取消订单
    @PutMapping("/cancel/{orderId}")
    public Result setOrderStatus(@PathVariable String orderId){
        ordersService.setOrderStatus(orderId,OrdersStatusEnums.ALREADY_CANCEL.getCode());
        //恢复库存
        ordersGoodsService.restoreBalanceById(orderId);
        //删除缓存
        OrdersVo ov = ordersService.getOrderById(orderId);
        String id = ov.getOrders().getId();
        LocalDateTime createTime = ov.getOrders().getCreateTime();
        OrderIsTimeoutUtils otu=new OrderIsTimeoutUtils();
        otu.setOrderId(id);
        otu.setCreateTime(createTime);
        String json = JSONUtil.toJsonStr(otu);
        redisTemplate.opsForList().remove(RedisKeyConstant.ORDER_IS_TIMEOUT_KEY,1, json);
        return Result.success();
    }
    @PostMapping("/status")
    //获取不同状态的订单
    public Result getOrdersByStatus(@RequestBody OrdersPageDto pageDto){
        OrdersVoList ovl = ordersService.getOrdersByStatus(pageDto);
        return  Result.success(ovl);
    }
    //确认支付
    @PutMapping("/pay/{id}")
    public Result confirmPay(@PathVariable String id){
        ordersService.confirmPay(id);
        return Result.success();
    }
    //再来一单
    @GetMapping("/oneMore/{orderId}")
    public Result oneMoreOrder(@PathVariable String orderId){
        return ordersService.oneMoreOrder(orderId);
    }
}
