package com.gulimall.guliproduct.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: duhang
 * @Date: 2022/6/17
 * @Description:
 **/
@Configuration
public class RedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(){
        //创建配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.247.129:6379");
        //创建实例返回
        return Redisson.create(config);
    }
}
