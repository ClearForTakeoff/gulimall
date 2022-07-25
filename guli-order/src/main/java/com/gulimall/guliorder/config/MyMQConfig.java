package com.gulimall.guliorder.config;

import com.gulimall.guliorder.entity.OrderEntity;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.Topic;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: duhang
 * @Date: 2022/7/10
 * @Description:
 **/

@Configuration
public class MyMQConfig {

    //创建交换机，队列，绑定关系
    @Bean
    public Exchange orderEventExchange(){
        //创建交换机
        //(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        TopicExchange topicExchange = new TopicExchange("order.event.exchange", true, false);
        return topicExchange;
    }

    @Bean
    public Queue orderDelayQueue(){
        //属性设置
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order.event.exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000); // 消息过期时间 1分钟
        Queue queue = new Queue("order.delay.queue", true, false, false,arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseQueue(){

        return new Queue("order.release.queue",true,false,false);
    }

    @Bean
    public Binding orderDelayBinding(){
        //建立绑定关系
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			@Nullable Map<String, Object> arguments) {
        //目的地，目的地类型，交换机名，路由键
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,"order.event.exchange",
                "order.create.order",null);
    }

    @Bean
    public Binding orderReleaseBinding(){
        //目的地，目的地类型，交换机名，路由键
        return new Binding("order.release.queue", Binding.DestinationType.QUEUE,"order.event.exchange",
                "order.release.order",null);

    }

    //新建释放订单与库存的绑定关系
    @Bean
    public Binding orderReleaseBindingStockRelease(){
        //目的地，目的地类型，交换机名，路由键
        return new Binding("stock.release.queue", Binding.DestinationType.QUEUE,"order.event.exchange",
                "order.release.other.#",null);

    }


    //创建秒杀队列和绑定关系

    @Bean
    public Queue seckillOrderQueue(){
        return new Queue("order.seckill.queue",true,false,false);
    }

    @Bean
    public Binding seckillQueueExchangeBinding(){
        return new Binding("order.seckill.queue", Binding.DestinationType.QUEUE,"order.event.exchange",
                "order.seckill.order",null);
    }
}
