package com.gulimall.guliseckill.service;

import com.gulimall.guliseckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 **/

public interface SeckillService {
    //上架秒杀商品
    public void uploadSeckillSku();

    //查询当前时间的秒杀商品
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    //查询商品的秒杀信息
    SeckillSkuRedisTo getSkuSeckill(Long skuId);

    //秒杀
    String kill(String killId, String key, Integer num);
}
