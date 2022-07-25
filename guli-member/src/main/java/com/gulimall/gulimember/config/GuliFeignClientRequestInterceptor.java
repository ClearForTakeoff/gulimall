package com.gulimall.gulimember.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description: 配置请求拦截器
 **/
@Configuration
public class GuliFeignClientRequestInterceptor {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                 //使用threadlocal获取到请求数据
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes  != null){
                    HttpServletRequest request = requestAttributes.getRequest();
                    //请求头数据,同步cookie
                    String cookies = request.getHeader("Cookie");
                    template.header("Cookie",cookies);
                }
            }
        };
    }
}
