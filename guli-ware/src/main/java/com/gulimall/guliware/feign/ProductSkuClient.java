package com.gulimall.guliware.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: duhang
 * @Date: 2022/6/8
 * @Description:
 *
 **/
@FeignClient("guli-product")
public interface ProductSkuClient {

    @RequestMapping("/guliproduct/skuinfo/info/{skuId}")
    //@RequiresPermissions("guliproduct:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);
}
