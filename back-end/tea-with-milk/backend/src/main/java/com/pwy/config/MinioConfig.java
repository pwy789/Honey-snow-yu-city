package com.pwy.config;

import com.pwy.common.MinioProperties;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class MinioConfig {
    @Resource
    private MinioProperties properties;
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder().
                endpoint(properties.getEndpoint()).
                credentials(properties.getAccessKey(),properties.getSecretKey()).build();
    }

}