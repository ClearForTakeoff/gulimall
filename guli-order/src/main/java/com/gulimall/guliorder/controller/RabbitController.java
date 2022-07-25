package com.gulimall.guliorder.controller;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Controller;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
//@Controller
//@RabbitListener(queues = {"myRabbitMqQueue"})
public class RabbitController {

    //@RabbitHandler
    public void listener(Message message, Object entity, Channel channel){
        System.out.println("队列接收到消息："+entity);

    }


}
