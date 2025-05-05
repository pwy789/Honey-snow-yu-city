package com.pwy;

import com.pwy.service.impl.OrdersServiceImpl;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class MiniProgramTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testRedis(){
        redisTemplate.opsForValue().set("name","彭文宇");
    }

    @Test
    void testCreateOrderId(){
        long l = LocalDateTime.of(2025, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        System.out.println(l);
    }
    @Test
    void testDistance(){
        OrdersServiceImpl impl=new OrdersServiceImpl();
        double lo1=113.082578;
        double la1=28.320408;
        double lo2=112.855911;
        double la2=28.213721;
        Vector3D point1 = impl.getCartesianCoordinates(lo1, la1, 6371000);
        Vector3D point2 = impl.getCartesianCoordinates(lo2, la2, 6371000);
        System.out.println("两点的距离是: "+point1.distance(point2));
    }
    private final ExecutorService SINGLE_THREAD_POOL= Executors.newSingleThreadExecutor();
    @Test
    void testThreadPool(){
        System.out.println(SINGLE_THREAD_POOL);
    }
}
