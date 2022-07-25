package com.gulimall.guliseckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: duhang
 * @Date: 2022/7/6
 * @Description:
 **/
@Configuration
public class RabbitConfig {


    //自定义messageConverter
    @Bean
    public MessageConverter messageConverter(){
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        return jackson2JsonMessageConverter;
    }

   
}
