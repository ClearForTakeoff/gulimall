package com.gulimall.guliorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
@Configuration
public class RedisSessionConfig {
    //配置cookie的作用域
    @Bean
    public CookieSerializer cookieSerializer(){
        //接口只有一个实现类
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName("gulimall.com");
        defaultCookieSerializer.setCookieName("gulimallSession");
        return defaultCookieSerializer;
    }

    //配置redis的序列化器
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
