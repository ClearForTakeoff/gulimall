package com.gulimall.gulimember;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("com.gulimall.gulimember.dao")
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
public class GuliMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMemberApplication.class, args);
    }

}
