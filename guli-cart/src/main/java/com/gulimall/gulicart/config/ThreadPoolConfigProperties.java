package com.gulimall.gulicart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: duhang
 * @Date: 2022/6/29
 * @Description: 绑定到线程池的配置
 **/
@ConfigurationProperties(prefix = "gulimall.thread") //配置文件中的前缀
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer MaxSIze;
    private Integer keepAlive;
}
