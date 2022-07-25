package com.gulimall.gulicart.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description:
 **/
@FeignClient("guli-product")
public interface ProductFeignClient {
    @RequestMapping("/guliproduct/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    //查询sku属性值
    @GetMapping("/guliproduct/skusaleattrvalue/getSkuAttrStrList/{skuId}")
    public List<String> getSkuSaleAttr(@PathVariable("skuId") Long skuId);

    //查询sku'价格
    @RequestMapping("guliproduct/skuinfo/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId")Long skuId);
}
