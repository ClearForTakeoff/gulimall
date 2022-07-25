package com.gulimall.guliware.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: duhang
 * @Date: 2022/7/12
 * @Description:
 **/
@FeignClient("guli-order")
public interface OrderClient {

    @GetMapping("/guliorder/order/orderStatus/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn")String orderSn);
}
