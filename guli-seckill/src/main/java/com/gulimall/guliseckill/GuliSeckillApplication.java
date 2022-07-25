package com.gulimall.guliseckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 整合 sentinel
 *      （1）引入依赖
 *      （2）启动客户端
 *      (3)配置控制台地址
 *      (4)导入 信息审计模块  spring-boot-starter-actuator
 *      （5）配置 # 导入每个微服务
 *                  management.endpoints.web.exposure.include=*
 *       （6）自定义，流控返回数据
 *
 *     使用sentinel保护feign远程调用
 *      实现 远程调用的接口，指定回调类型
 *      指定远程服务的降级策略
 *
 *      自定义降级资源
 *          try(Entry entry = SphU.entry("seckillSkus"))
 *      基于注解的资源降级
 *      @SentinelResource(blockHandler = "blockHandlerForGetUser")
 *          任意方法上加入注解，指定降级的，
 *          触发流控降级时调用的方法：blockHandlerForGetUser
 *          方法返回值，形参与原方法一样，多一个参数 BlockException
 *           public List<SeckillSkuRedisTo> seckillSkuHandler(BlockException e){
 *
 */
@EnableRabbit
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gulimall.guliseckill.client")
@EnableRedisHttpSession
public class GuliSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliSeckillApplication.class, args);
    }

}
