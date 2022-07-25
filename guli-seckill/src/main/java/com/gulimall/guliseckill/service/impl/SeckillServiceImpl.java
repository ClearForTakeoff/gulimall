package com.gulimall.guliseckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.common.to.MemberResponseVo;
import com.common.to.SeckillOrderTo;
import com.common.utils.R;
import com.gulimall.guliseckill.client.CouponClient;
import com.gulimall.guliseckill.client.ProductClient;
import com.gulimall.guliseckill.interceptor.UserLoginInterceptor;
import com.gulimall.guliseckill.service.SeckillService;
import com.gulimall.guliseckill.to.SeckillSkuRedisTo;
import com.gulimall.guliseckill.vo.SeckillSessionVo;
import com.gulimall.guliseckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 **/
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SECKillSKU_CACHE_PREFIX = "seckill:skus:";
    //每个商品对应的分布式锁信号量前缀名
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; //加商品随机码

    @Autowired
    CouponClient couponClient;

    @Autowired
    ProductClient productClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient; //分布式redisson客户端

    @Autowired
    RabbitTemplate rabbitTemplate;

    //扫描秒杀商品
    @Override
    public void uploadSeckillSku() {
        //得到秒杀的数据
        //优惠服务封装的 秒杀活动信息，和活动商品信息
        R latestThreeDaysSku = couponClient.getLatestThreeDaysSku();
        if(latestThreeDaysSku.getCode() == 0){
            List<SeckillSessionVo> data = latestThreeDaysSku.getData(new TypeReference<List<SeckillSessionVo>>(){});
            //缓存数据
            //缓存秒杀活动信息
            saveSecKillSessionInfo(data);
            //缓存秒杀商品信息
            saveSecKillSkuInfo(data);
        }

    }

    //查询当前时间的秒杀商品
    @Override
    @SentinelResource(blockHandler = "seckillSkuHandler")
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //1.获取当前时间
        long time = new Date().getTime();
        try(Entry entry = SphU.entry("seckillSkus")){
            Set<String> keys = redisTemplate.keys("seckill:sessions:*");
            //截取字符串
            for (String key : keys) {
                String[] split = key.split(":");
                //得到字符串的时间范围
                String[] timeZone = split[2].split("_");
                long start = Long.parseLong(timeZone[0]);
                long end = Long.parseLong(timeZone[1]);
                //当前时间在区间范围内
                if (time >= start && time <= end) {
                    //拿到value值，场次id和商品id
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100); //从key中取值
                    //根据值，再去拿商品信息
                    BoundHashOperations<String, String, Object> operations =
                            redisTemplate.boundHashOps(SECKillSKU_CACHE_PREFIX);
                    List<Object> infos = operations.multiGet(range);//取到商品信息
                    if(infos != null){
                        //把字符数据转成对象
                        List<SeckillSkuRedisTo> redisTos = infos.stream().map(info -> {
                            SeckillSkuRedisTo source = JSON.parseObject(info.toString(), SeckillSkuRedisTo.class);
                            return source;
                        }).collect(Collectors.toList());
                        return redisTos;
                    }
                }
            }
        }catch (BlockException e){
            log.error("资源被限流");
        }
        return null;
    }

    public List<SeckillSkuRedisTo> seckillSkuHandler(BlockException e){
        log.error("方法getCurrentSeckillSkus被限流");
        return null;
    }


    @Override
    public String kill(String killId, String key, Integer num) {
        //
        MemberResponseVo memberResponseVo = UserLoginInterceptor.threadLocal.get();
        //获取秒杀商品的详细信息
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SECKillSKU_CACHE_PREFIX);
        String s = operations.get(killId);
        if (!StringUtils.isEmpty(s)) {
            SeckillSkuRedisTo redisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);
            //验证时间
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();
            if(time >= startTime && time <= endTime){
                //校验随机码
                String randomCode = redisTo.getRandomCode();
                String s1 = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                if(randomCode.equals(key) && killId.equals(s1)){
                    //验证,购买数量需要小于限制数量
                    if(num <= redisTo.getSeckillLimit().intValue()){
                        //验证，是否已经购买过
                        //秒杀成功，占位
                        String redisKey = memberResponseVo.getId() + "_" +  redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                        //设置自动过期，为场次结束时间
                        long ttl = endTime - time ;
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        //如果占位成功，说明没有买过
                        if(aBoolean){
                            //减信号量
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + key);
                            //尝试减信号量，设置了等待时间
                            try {
                                boolean acquire = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                                //减信号量成功
                                if(acquire){
                                    //下单购买，返回订单号
                                    String timeId = IdWorker.getTimeId();
                                    //封装秒杀订单
                                    SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                                    seckillOrderTo.setOrderSn(timeId);
                                    seckillOrderTo.setSkuId(redisTo.getSkuId());
                                    seckillOrderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                    seckillOrderTo.setNum(num);
                                    seckillOrderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                    seckillOrderTo.setMemberId(memberResponseVo.getId());
                                    //发消息
                                    rabbitTemplate.convertAndSend("order.event.exchange","order.seckill.order",seckillOrderTo);
                                    return timeId;
                                }else{
                                    return null;
                                }
                            } catch (InterruptedException e) {
                                //没有拿到信号量
                                return null;
                            }
                        }else{
                            return null;
                        }
                    }
                }else{
                    return null;
                }
            }
        }
        return null;
    }

    //查询商品的秒杀信息
    @Override
    public SeckillSkuRedisTo getSkuSeckill(Long skuId) {
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SECKillSKU_CACHE_PREFIX);
        Set<String> keys = operations.keys();//拿到key集合
        if(keys !=null && keys.size() > 0){

            for (String key : keys) {
                //分割
                String[] split = key.split("_");
                //匹配上了
                if(split[1].equals(skuId.toString())){
                    //返回数据
                    String skuJson = operations.get(key);
                    SeckillSkuRedisTo redisTo = JSON.parseObject(skuJson, SeckillSkuRedisTo.class);
                    //处理随机码，秒杀时间内，带上随机码，秒杀时间外，不带上随机码
                    Long startTime = redisTo.getStartTime();
                    Long endTime = redisTo.getEndTime();
                    long now = new Date().getTime();
                    //当前时间不在秒杀时间内
                    if(now <= startTime || now >= endTime ){
                        redisTo.setRandomCode(null);
                    }
                    return redisTo;
                }
            }
        }
        return null;
    }

    //缓存活动商品
    private void saveSecKillSkuInfo(List<SeckillSessionVo> data) {
        //遍历秒杀活动信息
        data.forEach(item->{
            BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(SECKillSKU_CACHE_PREFIX);
            //遍历每个秒杀活动对应的商品，保存
            item.getRelationEntities().stream().forEach(sku->{
                //设置随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                if(!operations.hasKey(sku.getPromotionSessionId()+"_"+sku.getSkuId().toString())) { //redis中没有商品数据，添加商品
                    //保存,skuredis基本信息
                    SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                    //秒杀的商品信息
                    BeanUtils.copyProperties(sku, seckillSkuRedisTo);
                    seckillSkuRedisTo.setStartTime(item.getStartTime().getTime());
                    seckillSkuRedisTo.setEndTime(item.getEndTime().getTime());
                    seckillSkuRedisTo.setRandomCode(token);
                    //sku的具体信息
                    //远程查询skuinfo
                    R info = productClient.info(sku.getSkuId());
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getDataByName("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        //转为json字符串
                        seckillSkuRedisTo.setSkuInfo(skuInfo);
                    }
                    String jsonString = JSON.toJSONString(seckillSkuRedisTo);
                    //存放场次对应的商品id，带上场次的信息，避免多个场次有相同id时，不能存放
                    operations.put(sku.getPromotionSessionId()+"_"+sku.getSkuId().toString(), jsonString);
                    //信号量的key是唯一的，因此判断有无库存信号量，与商品的判断一致
                    //分布式redisson，设置分布式锁，信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //分布式锁，设置信号量为商品秒杀的数量
                    semaphore.trySetPermits(sku.getSeckillCount().intValue());
                }
            });
        });
    }

    //缓存活动信息
    private void saveSecKillSessionInfo(List<SeckillSessionVo> data) {
        //获取到商品的信息,时间和商品id
        data.stream().forEach(session->{
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            //redis key
            String key = startTime + "_" +endTime;
            //缓存活动信息
            //如果有了，
            Boolean hasKey = redisTemplate.hasKey(SESSIONS_CACHE_PREFIX + key);
            if(!hasKey){
                //存放场次对应的商品id，带上场次的信息，避免多个场次有相同id时，不能存放
                List<String> skuIds = session.getRelationEntities().stream().map(item ->{return item.getPromotionSessionId()+"_"+item.getSkuId().toString();}).collect(Collectors.toList());
                if(skuIds != null){
                    redisTemplate.opsForList().leftPushAll(SESSIONS_CACHE_PREFIX+key,skuIds);

                }
            }
        });
    }
}
