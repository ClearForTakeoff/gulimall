package com.gulimall.guliorder.client;

import com.common.to.SpuInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: duhang
 * @Date: 2022/7/9
 * @Description:
 **/
@FeignClient("guli-product")
public interface ProductClient {

    //根据skuid找到spu信息
    @GetMapping("guliproduct/spuinfo/getSpuInfoBySkuId/{skuId}")
    SpuInfoTo getSpuInfoBySkuId(@PathVariable("skuId")Long skuId);
}
