package com.gulimall.gulicoupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;


@SpringBootApplication
@MapperScan("com.gulimall.gulicoupon.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gulimall.gulicoupon")
public class GuliCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliCouponApplication.class, args);
    }

}
