package com.gulimall.guliproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
/*
    1.整合redis使用缓存
        （1）引入依赖
        （2）配置redis地址端口
        (3)redisTemplate
    2.整合redisson做分布式锁
        (1)引入依赖
        (2)配置类，注入操作redisson的对象,设置redis地址端口
        (3)redissonClient

    3.缓存一致性：整合springcache
    (1)引入依赖
        spring-boot-starter-cache，spring-boot-starter-data-redis
    (2)配置
        缓存的注解
        @Cacheable: 触发把数据保存到缓存
        @CacheEvict: 将数据从缓存删除
        @CachePut：更新缓存，不影响方法更新
        @Caching：组合多个缓存操作
        @CacheConfig：共享相同的缓存配置

        开启缓存功能
        @EnableCaching

        @Cachable()
        默认属性 缓存分区名字，key=‘缓存命名编号’，可以使用spel表达式取值
        设置存活时间 配置文件 time-to-live 单位ms



 */

//@MapperScan("com.gulimall.guliproduct.dao")
//@EnableCaching //开启缓存功能
@EnableRedisHttpSession
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gulimall.guliproduct.feign")
public class GuliProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliProductApplication.class, args);
    }

}
