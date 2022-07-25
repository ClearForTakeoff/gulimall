package com.gulimall.guliseckill.config;


import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.WebFluxCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: duhang
 * @Date: 2022/7/16
 * @Description:
 **/
@Configuration
public class SentinelConfig {
    public  SentinelConfig(){

    }
}
