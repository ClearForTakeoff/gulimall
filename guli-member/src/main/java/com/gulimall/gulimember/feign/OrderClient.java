package com.gulimall.gulimember.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: duhang
 * @Date: 2022/7/14
 * @Description:
 **/
@FeignClient("guli-order")
public interface OrderClient {

    //查询会员的订单
    @PostMapping("/guliorder/order/listOrder")
    public R getOrderList(@RequestBody Map<String, Object> params);
}
