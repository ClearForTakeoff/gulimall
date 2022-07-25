package com.gulimall.gulicart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: duhang
 * @Date: 2022/6/29
 * @Description: 异步线程池配置
 **/

@EnableConfigurationProperties(ThreadPoolConfigProperties.class) //开启属性配置
@Configuration
public class MyThreadConfig {
    @Autowired
    ThreadPoolConfigProperties threadPoolConfigProperties;
    //创建异步线程池
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        //构造器参数
        //                              (int corePoolSize, 线程池核心线程数
        //                              int maximumPoolSize,  最大线程数
        //                              long keepAliveTime, 空闲线程存活时间
        //                              TimeUnit unit, 时间单位
        //                              BlockingQueue<Runnable> workQueue) 阻塞队列
        //                              ThreadFactory threadFactory,线程工厂
        //                              RejectedExecutionHandler handler
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadPoolConfigProperties.getCoreSize(),
                threadPoolConfigProperties.getMaxSIze(),
                threadPoolConfigProperties.getKeepAlive(),
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(100000),Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        return threadPoolExecutor;
    }
}
