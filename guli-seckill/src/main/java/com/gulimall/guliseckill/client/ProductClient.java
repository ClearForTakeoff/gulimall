package com.gulimall.guliseckill.client;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 **/
@FeignClient("guli-product")
public interface ProductClient {
    @RequestMapping("guliproduct/skuinfo/info/{skuId}")
    //@RequiresPermissions("guliproduct:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);
}
