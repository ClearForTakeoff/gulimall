package com.gulimall.guliproduct.feign.fallback;

import com.common.exception.BizCodeEnum;
import com.common.utils.R;
import com.gulimall.guliproduct.feign.SecondKillClient;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: duhang
 * @Date: 2022/7/16
 * @Description:
 **/
//实现SecondKillClient远程服务调用失败的回调
@Component
@Slf4j
public class SecondKillClientFallback implements SecondKillClient {
    @Override
    public R getSkuSecKill(Long skuId) {
        log.info("服务熔断：seckill");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
