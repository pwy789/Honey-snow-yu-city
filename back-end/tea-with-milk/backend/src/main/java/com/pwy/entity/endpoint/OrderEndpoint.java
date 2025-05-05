package com.pwy.entity.endpoint;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.pwy.common.RedisKeyConstant;
import com.pwy.config.SpringConfigurator;
import com.pwy.entity.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@ServerEndpoint(value = "/orderSocket",configurator = SpringConfigurator.class)
@Component
public class OrderEndpoint {

    //这个map存储不同店铺的id,因为需要将用户在指定店铺下的订单推送给对应的店铺后台
    private final Map<String,Session> shopSession=new ConcurrentHashMap<>();

    private final ExecutorService SINGLE_THREAD_POOL= Executors.newSingleThreadExecutor();

    private  boolean shutdownThreadPoolFlag=false;

    //标志位，用于判断任务是否已经启动
    private static boolean taskStarted = false;

    @PreDestroy
    public void destroy() {
        shutdownThreadPoolFlag=true;
        SINGLE_THREAD_POOL.shutdown();
        try {
            if (!SINGLE_THREAD_POOL.awaitTermination(10, TimeUnit.SECONDS)) {
                SINGLE_THREAD_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            SINGLE_THREAD_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }
    @Autowired
    private StringRedisTemplate redisTemplate;
    //建立连接时触发
    @OnOpen
    public void onOpen(Session session) {

     //执行任务
        synchronized (OrderEndpoint.class) {
            if (!taskStarted) {
                SINGLE_THREAD_POOL.execute(new Task());
                taskStarted = true;
            }
        }
    }
    //收到前端传来的数据时触发
    @OnMessage
    public void onMessage(String message,Session session) {
        //存储会话
        shopSession.put(message,session);
    }
    //关闭连接时触发
    @OnClose
    public void onClose(Session session){

    }
    //内部任务类
private class Task implements Runnable{

    @Override
    public void run() {

        while (true){

            try {
                if(shutdownThreadPoolFlag) break;
                String stream = RedisKeyConstant.ORDER_MESSAGE_QUEUE;
                Consumer consumer=Consumer.from("group01","consumer01");
                List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(consumer, StreamReadOptions.empty().block(Duration.ofSeconds(5)), StreamOffset.create(stream, ReadOffset.lastConsumed()));
                if(list==null||list.isEmpty()){
                    continue;
                }
                //获取数据,这个value就是之前向里面存的message
                MapRecord<String, Object, Object> record = list.get(0);
                Map<Object, Object> value = record.getValue();
                OrderMessage message = BeanUtil.fillBeanWithMap(value, new OrderMessage(), false);
                log.info("message: {}",message);
                //将这个消息转发给后台
                String shopId = message.getShopId();
                Session session = shopSession.get(shopId);
                if(session==null){
                    //说明该订单下的店铺还没有与后端建立websocket连接,直接确认消息就行了
                 redisTemplate.opsForStream().acknowledge(RedisKeyConstant.ORDER_MESSAGE_QUEUE,"group01",record.getId());
                    return;
                }
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
                //确认消息
                redisTemplate.opsForStream().acknowledge(RedisKeyConstant.ORDER_MESSAGE_QUEUE,"group01",record.getId());
                //删除消息
                redisTemplate.opsForStream().delete(RedisKeyConstant.ORDER_MESSAGE_QUEUE,record.getId());
            } catch (IOException e) {
                e.printStackTrace();
                //如果出现了异常,此时该消息已经被消费了，因此需要去pending-list中找到该消息并处理
                handlerPendingList();
            }

        }
    }
}

    private void handlerPendingList() {
        while (true){
            try {
                String stream = RedisKeyConstant.ORDER_MESSAGE_QUEUE;
                Consumer consumer=Consumer.from("group01","consumer01");
                List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(consumer, StreamReadOptions.empty().block(Duration.ofSeconds(5)), StreamOffset.create(stream, ReadOffset.from("0")));
                if(list==null||list.isEmpty()){
                    //说明数据已经处理完了,退出
                    break;
                }
                //获取数据,这个value就是之前向里面存的message
                MapRecord<String, Object, Object> record = list.get(0);
                Map<Object, Object> value = record.getValue();
                OrderMessage message = BeanUtil.fillBeanWithMap(value, new OrderMessage(), false);
                //将这个消息转发给后台
                String shopId = message.getShopId();
                Session session = shopSession.get(shopId);
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
                //确认消息
                redisTemplate.opsForStream().acknowledge(RedisKeyConstant.ORDER_MESSAGE_QUEUE,"group01",record.getId());
                //删除消息
                redisTemplate.opsForStream().delete(RedisKeyConstant.ORDER_MESSAGE_QUEUE,record.getId());
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
