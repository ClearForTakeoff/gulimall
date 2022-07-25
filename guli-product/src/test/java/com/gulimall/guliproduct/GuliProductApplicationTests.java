package com.gulimall.guliproduct;


import com.gulimall.guliproduct.entity.BrandEntity;
import com.gulimall.guliproduct.service.BrandService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;


@SpringBootTest
class GuliProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {
        System.out.println(LocalDateTime.now());
    }

    @Autowired
    RedissonClient redissonClient;
    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }




}
