package com.pwy.utils;

import cn.hutool.json.JSONUtil;
import com.pwy.common.RedisKeyConstant;
import com.pwy.enums.OrdersStatusEnums;
import com.pwy.service.OrdersGoodsService;
import com.pwy.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


//定时器类
@Slf4j
@Component
public class OrderScheduledTask {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersGoodsService ordersGoodsService;
    @Autowired
    private StringRedisTemplate template;
    //5分钟
    private final long orderDeliveryTimeout=1000*60*5;
    //此定时任务用于从list集合中拉取订单，判断订单是否超时,每隔5秒执行一次
    @Scheduled(fixedRate = 5000)
    public void checkOrderIsaTimeout(){
        String orderJSon = template.opsForList().index(RedisKeyConstant.ORDER_IS_TIMEOUT_KEY, 0);
        if(orderJSon==null){
            return;
        }
        OrderIsTimeoutUtils otu = JSONUtil.toBean(orderJSon, OrderIsTimeoutUtils.class);
        LocalDateTime createTime = otu.getCreateTime();
        if(createTime.plusMinutes(10L).isBefore(LocalDateTime.now())){
            String orderId = otu.getOrderId();
            log.info("订单号为{}的订单已经超时取消",orderId);
            //说明这个订单已经超时了
            //更新订单状态为1(已取消)
      ordersService.setOrderStatus(orderId,OrdersStatusEnums.ALREADY_CANCEL.getCode());
           //恢复库存
            ordersGoodsService.restoreBalanceById(orderId);
            //删除该订单数据
            template.opsForList().remove(RedisKeyConstant.ORDER_IS_TIMEOUT_KEY,1,orderJSon);
        }
    }
    //此定时任务用于从zSet集合从取出订单，判断订单配送是否超过了5分钟，并将其修改为已送达(每五分钟执行一次)
    @Scheduled(cron = "0 0/5 * * * ?")
    public  void checkOrderDeliveryIsTimeOut(){
        Set<ZSetOperations.TypedTuple<String>> typedTuples = template.opsForZSet().rangeByScoreWithScores(RedisKeyConstant.ORDER_DELIVERY_KEY, 0, Instant.now().toEpochMilli());
       if( typedTuples.isEmpty()) return;
        List<ZSetOperations.TypedTuple<String>> collect = typedTuples.stream().collect(Collectors.toList());
        for (ZSetOperations.TypedTuple<String> tuple : collect) {
            Double score = tuple.getScore();
            String OrderId = tuple.getValue();
            if(score+orderDeliveryTimeout<Instant.now().toEpochMilli());{
                log.info("订单号为:{}的订单已经送达",OrderId);
                //设置该订单为已送达
                ordersService.setOrderStatus(OrderId,OrdersStatusEnums.SUCCESSFULLY_DELIVERED.getCode());
                //从队列中移除该数据
                template.opsForZSet().remove(RedisKeyConstant.ORDER_DELIVERY_KEY,OrderId);
            }
        }
    }
}
