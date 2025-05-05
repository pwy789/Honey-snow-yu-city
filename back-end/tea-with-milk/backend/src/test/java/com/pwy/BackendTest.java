package com.pwy;


import com.pwy.common.RedisKeyConstant;
import com.pwy.utils.MinioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class BackendTest {
    @Value("${spring.servlet.multipart.max-file-size}")
private  String FILE_MAX_SIZE;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MinioService minioService;
    @Test
    void createBucket() throws Exception {
       minioService.createBucket("milky-tea");
    }
    @Test
    void testSize(){
        String maxSizeNum = FILE_MAX_SIZE.substring(0, 1);
        long maxSize = Long.parseLong(maxSizeNum);
        System.out.println(maxSize);
    }
    @Test
    void testRedisTemplate(){
        System.out.println(redisTemplate);
    }

    @Test
   void testOrderTotal(){
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
         ScanOptions.scanOptions().match("icr:order:pickUp:"+format).build();
    }
}
