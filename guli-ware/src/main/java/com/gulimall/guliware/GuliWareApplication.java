package com.gulimall.guliware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@SpringBootApplication
@MapperScan("com.gulimall.guliware.dao")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients("com.gulimall.guliware")
public class GuliWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliWareApplication.class, args);
    }

}
