package com.gulimall.guliproduct.feign;

import com.common.to.HasStockTo;
import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/14
 * @Description: 仓库服务远程哭护短
 **/
@FeignClient("guli-ware")
public interface WareFeignClient {
    //远程调用，查询sku是否有库存
    @RequestMapping("/guliware/waresku/hasStock")
    R hasStock(@RequestBody List<Long> skuIds);
}
