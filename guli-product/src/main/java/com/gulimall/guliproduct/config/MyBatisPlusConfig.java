package com.gulimall.guliproduct.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * author:admin
 * date:2022/5/24
 * Info:
 **/

@Configuration
@EnableTransactionManagement//开启事务
@MapperScan("com.gulimall.guliproduct.dao")
public class MyBatisPlusConfig {
    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setOverflow(true);
        return paginationInterceptor;

    }
}
