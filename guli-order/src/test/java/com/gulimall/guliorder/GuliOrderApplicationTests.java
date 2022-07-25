package com.gulimall.guliorder;

import com.gulimall.guliorder.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GuliOrderApplicationTests {


    @Autowired
    AmqpAdmin amqpAdmin; //管理组件

    @Autowired
    RabbitTemplate rabbitTemplate;//发送消息
    //测试创建队列，交换机，绑定关系
    //测试收发消息
    @Test
    void createExchange() {
        //创建交换机
        DirectExchange exchange = new DirectExchange("myRabbitExchange",false,false);
        amqpAdmin.declareExchange(exchange);
        System.out.println("交换机创建成功：" + exchange.getName());
    }

    @Test
    void createQueue(){

        Queue queue  =new Queue("myRabbitMqQueue",false,false,false);
        amqpAdmin.declareQueue(queue);
        System.out.println("队列创建成功：" + queue.getName());
    }

    //创建绑定关系
    @Test
    void createBinding(){
        Binding binding = new Binding("myRabbitMqQueue", Binding.DestinationType.QUEUE,
                "myRabbitExchange","myQueue",null);
        amqpAdmin.declareBinding(binding);
        System.out.println("绑定关系创建成功：" + binding);
    }

    //测试消息发送，与接收
    @Test
    void testSendMessage(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(111L);
        orderEntity.setOrderSn("sadmop");

        rabbitTemplate.convertAndSend("myRabbitExchange","myQueue",orderEntity);
    }
}
