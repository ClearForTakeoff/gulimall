package com.gulimall.guliseckill.scheduledTask;

import com.gulimall.guliseckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 *
 *      定时任务
 *          （1）@EnableScheduling
 *          (2)@Scheduled
 *      异步任务
 *          (1)@EnableAsync
 *          (2)@Async
 *
 **/
@Slf4j
@Service
public class ScheduledTask {
    @Autowired
    SeckillService service;

    @Autowired
    RedissonClient redissonClient;
    //锁
    private final String UPLOAD_LOCK = "seckill:upload:lock";
    //定时任务 ，每天凌晨三点执行
    @Scheduled(cron = "*/5 * * * * ? ")
    public void upLoadSecKillSku(){
        //分布式锁，在分布式场景下，多个相同的服务，值允许运行一次上架
        //占锁
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        //占锁时间
        lock.lock(10, TimeUnit.SECONDS);
        try {
            //
            service.uploadSeckillSku();
        } finally {
            //最终解锁
            lock.unlock();
        }
    }
}
