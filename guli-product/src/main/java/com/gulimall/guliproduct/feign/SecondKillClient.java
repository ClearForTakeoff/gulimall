package com.gulimall.guliproduct.feign;

import com.common.utils.R;
import com.gulimall.guliproduct.feign.fallback.SecondKillClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 **/
@FeignClient(value = "guli-seckill",fallback = SecondKillClientFallback.class) //远程调用失败的回调
public interface SecondKillClient {
    @GetMapping(path = "/skuSeckillInfo/{skuId}")
    public R getSkuSecKill(@PathVariable("skuId")Long skuId);
}
