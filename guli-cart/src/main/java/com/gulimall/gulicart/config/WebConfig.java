package com.gulimall.gulicart.config;

import com.gulimall.gulicart.interceptor.CartInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description:
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    CartInterceptor cartInterceptor;
    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(cartInterceptor).addPathPatterns("/**"); //p配置拦截所有的请求
    }
}
