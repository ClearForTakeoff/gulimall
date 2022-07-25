package com.gulimall.guliauth.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: duhang
 * @Date: 2022/6/29
 * @Description:
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        /**
         * @MethodName: addViewControllers
         * @Param: [org.springframework.web.servlet.config.annotation.ViewControllerRegistry]
         * @Return:void
         * @Date: 2022-06-29
         * @Description : urlPath:请求路径，setViewName:转发页面
        **/
        registry.addViewController("/register.html").setViewName("register");
    }

}
