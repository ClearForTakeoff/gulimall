package com.gulimall.guliorder.client;

import com.common.utils.R;
import com.gulimall.guliorder.entity.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/8
 * @Description:
 **/
@FeignClient("guli-ware")
public interface WareClient {
    @RequestMapping("/guliware/waresku/hasStock")
    public R hasStock(@RequestBody List<Long> skuIds);

    //计算运费
    @GetMapping("/guliware/wareinfo/fare")
    public R getFare(@RequestParam("addrId")Long addrId);

    //锁库存的方法
    @PostMapping(value = "/guliware/waresku/lock/order")
    R orderLockStock(WareSkuLockVo lockVo);
}
