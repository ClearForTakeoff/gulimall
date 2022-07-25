package com.gulimall.gulimember.config;

import com.gulimall.gulimember.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: duhang
 * @Date: 2022/7/14
 * @Description:
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    UserLoginInterceptor userLoginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor);
    }
}
