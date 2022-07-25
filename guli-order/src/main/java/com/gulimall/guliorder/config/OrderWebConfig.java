package com.gulimall.guliorder.config;

import com.gulimall.guliorder.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
@Configuration
public class OrderWebConfig implements WebMvcConfigurer {
    //注入创建的拦截器
    @Autowired
    UserLoginInterceptor userLoginInterceptor;
    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(userLoginInterceptor).addPathPatterns("/**");//配置拦截所有请求
    }
}
