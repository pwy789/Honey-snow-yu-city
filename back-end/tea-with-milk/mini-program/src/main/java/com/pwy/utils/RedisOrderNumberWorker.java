package com.pwy.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RedisOrderNumberWorker {
    @Resource
    private StringRedisTemplate template;


    //唯一id 符号位0 31位时间戳 32位自增
    public long nextId(String keyPrefix){
        LocalDateTime now = LocalDateTime.now();

        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //2.生成序列号
        //3.拼接并返回
        return template.opsForValue().increment("icr:" + keyPrefix + ":" + date);
    }
}
