package com.gulimall.guliorder.listener;

import com.common.to.SeckillOrderTo;
import com.gulimall.guliorder.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @Author: duhang
 * @Date: 2022/7/16
 * @Description:
 **/
@Slf4j
@Component
@RabbitListener(queues = "order.seckill.queue")
public class SeckillOrderListener {
    @Autowired
    OrderService orderService;

    //秒杀订单进入队列后，拿到订单信息，创建到订单数据库去
    @RabbitHandler
    public void seckillOrder(SeckillOrderTo seckillOrderTo, Channel channel, Message message){
        log.info("秒杀订单++" + seckillOrderTo);
        //创建秒杀订单
        orderService.createSeckillOrder(seckillOrderTo);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
