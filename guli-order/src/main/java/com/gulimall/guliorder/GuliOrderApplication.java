package com.gulimall.guliorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *  1.分布式事务
 *      （1）导入依赖
 *      <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
 *         </dependency>
 *      (2) 分布式事务的方法上添加上注解
 *            @GlobalTransactional
 *      (3)使用代理包装数据源配置
 *            自定义数据源配置      public DataSource dataSource(DataSourceProperties dataSourceProperties){
 *            进行包装              return new DataSourceProxy(dataSource);
 *      （4)每个微服务都是要导入 registry.conf  file.conf
 *                 修改file.conf      vgroup_mapping.guli-order-fescar-service-group = "default"
 *       （5）分布式大事务 使用注解  @GlobalTransactional]
 *              远程的小事务 使用 @Transactional
 **/

@EnableAspectJAutoProxy(exposeProxy = true)     //开启了aspect动态代理模式,对外暴露代理对象
@EnableRedisHttpSession
@EnableFeignClients("com.gulimall.guliorder")
@EnableRabbit
@SpringBootApplication()
@MapperScan("com.gulimall.guliorder.dao")
@EnableDiscoveryClient
public class GuliOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliOrderApplication.class, args);
    }

}
