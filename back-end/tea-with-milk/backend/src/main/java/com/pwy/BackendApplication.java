package com.pwy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan({"com.pwy.mapper"})
@EnableTransactionManagement
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class,args);
    }
}
