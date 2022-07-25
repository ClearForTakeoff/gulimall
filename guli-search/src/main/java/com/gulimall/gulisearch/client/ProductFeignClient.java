package com.gulimall.gulisearch.client;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: duhang
 * @Date: 2022/6/23
 * @Description:
 **/
@FeignClient("guli-product")
public interface ProductFeignClient {

    @RequestMapping("/guliproduct/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);

}
