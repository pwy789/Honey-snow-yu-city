package com.pwy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwy.common.RedisKeyConstant;
import com.pwy.entity.dto.SearchDto;
import com.pwy.entity.pojo.Orders;
import com.pwy.entity.pojo.OrdersGoods;
import com.pwy.entity.pojo.User;
import com.pwy.entity.pojo.UserVoucher;
import com.pwy.entity.vo.EmployeeVo;
import com.pwy.entity.vo.OrdersGoodsVo;
import com.pwy.entity.vo.OrdersVo;
import com.pwy.entity.vo.PageOrdersVo;
import com.pwy.enums.OrdersStatusEnums;
import com.pwy.mapper.OrdersMapper;
import com.pwy.service.*;
import com.pwy.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrdersGoodsService ordersGoodsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVoucherService userVoucherService;

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public OrdersVo getOrdersById(String id) {
        Orders order = getById(id);
        OrdersVo ordersVo = BeanUtil.copyProperties(order, OrdersVo.class);
        LambdaQueryWrapper<OrdersGoods> lqw=new LambdaQueryWrapper<>();
        lqw.eq(OrdersGoods::getOrdersId,id);
        List<OrdersGoods> ogs = ordersGoodsService.list(lqw);
        List<OrdersGoodsVo> ogv = BeanUtil.copyToList(ogs, OrdersGoodsVo.class);
        ordersVo.setOgv(ogv);
        if(StringUtils.isBlank(order.getDeliveryFee())){
            //说明购买方式为外送,此时查询用于绑定的手机号等信息
            User user = userService.getById(order.getUserId());
           ordersVo.setName(user.getNickname());
           ordersVo.setPhone(user.getPhone());
        }
        //如果使用了优惠券,查询出优惠券的名称
        if(StringUtils.isNotBlank(order.getUserVoucherId())){
            UserVoucher uv = userVoucherService.getById(order.getUserVoucherId());
            //优惠券的id
            String voucherId = uv.getVoucherId();
            ordersVo.setVoucherName(voucherService.getById(voucherId).getName());
        }
        return ordersVo;
    }

    @Override
    public List<OrdersVo> getOrderByStatus(Short status) {
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        //查询出该店铺下的订单
        String shopId = ev.getShopId();
        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Orders::getShopId,shopId);
        if(status!=null){
            lqw.eq(Orders::getStatus,status);
        }
        lqw.orderByAsc(Orders::getOrderTime);
        List<Orders> orders = list(lqw);
        return query(orders);
    }

    private List<OrdersVo> query(List<Orders> orders) {
        List<OrdersVo> result = new ArrayList<>();
        for (Orders o : orders) {
            OrdersVo ov = BeanUtil.copyProperties(o, OrdersVo.class);
            //订单的主键id
            String id  = o.getId();
            List<OrdersGoods> ogs = ordersGoodsService.list(Wrappers.<OrdersGoods>lambdaQuery().eq(OrdersGoods::getOrdersId, id));
            List<OrdersGoodsVo> ogv = BeanUtil.copyToList(ogs, OrdersGoodsVo.class);
            ov.setOgv(ogv);
            if(StringUtils.isBlank(ov.getDeliveryFee())){
                User user = userService.getById(o.getUserId());
                ov.setName(user.getNickname());
                ov.setPhone(user.getPhone());
            }
            if(StringUtils.isNotBlank(o.getUserVoucherId())){
                UserVoucher uv = userVoucherService.getById(o.getUserVoucherId());
                //优惠券的id
                String voucherId = uv.getVoucherId();
                ov.setVoucherName(voucherService.getById(voucherId).getName());
            }
            result.add(ov);
        }
        return result;
    }

    @Override
    public PageOrdersVo searchOrderByCondition(SearchDto dto) {
        Page<Orders> p=new Page<>(dto.getPage(),dto.getPageSize());
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        //查询出该店铺下的订单
        String shopId = ev.getShopId();
        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Orders::getShopId,shopId);
         if(StringUtils.isNotBlank(dto.getOrderNum())){
             lqw.eq(Orders::getOrderId,dto.getOrderNum());
         }
         if(StringUtils.isNotBlank(dto.getBeginTime())){

             lqw.gt(Orders::getOrderTime,dto.getBeginTime());
         }
         if(StringUtils.isNotBlank(dto.getEndTime())){
             lqw.lt(Orders::getOrderTime,dto.getEndTime());
         }
         if(StringUtils.isNotBlank(dto.getStatus())){
             lqw.eq(Orders::getStatus,dto.getStatus());
         }
         lqw.orderByDesc(Orders::getCreateTime);
        Page<Orders> page = page(p, lqw);
        List<Orders> orders = page.getRecords();
        List<OrdersVo> ov = query(orders);
        long total = page.getTotal();
        PageOrdersVo pov=new PageOrdersVo();
        pov.setOv(ov);
        pov.setTotal(total);
        return pov;
    }

    @Override
    public void finishOrder(String id) {
        //首先要判断该订单是不是属于外送,判断标准是是否有小费的生成
        Orders order = getById(id);
        if(StringUtils.isBlank(order.getDeliveryFee())){
            //说明不是外送,修改订单状态为已完成即可
            order.setStatus(OrdersStatusEnums.COMPLETED.getCode());
            updateById(order);
            return;
        }
        //说明是外送,定时任务模拟外卖员配送的过程
        //首先将状态修改为配送中
        order.setStatus(OrdersStatusEnums.OUT_OF_DELIVERY.getCode());
        updateById(order);
        //存入zSet队列中,
        redisTemplate.opsForZSet().add(RedisKeyConstant.ORDER_DELIVERY_KEY,order.getId(), Instant.now().toEpochMilli());

    }

}
