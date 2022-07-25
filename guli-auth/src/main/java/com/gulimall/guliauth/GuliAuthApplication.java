package com.gulimall.guliauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession //开启redis存储session
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GuliAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliAuthApplication.class, args);
    }

}
