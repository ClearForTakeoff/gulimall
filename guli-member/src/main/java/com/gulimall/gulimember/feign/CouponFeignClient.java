package com.gulimall.gulimember.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * author:admin
 * date:2022/5/8
 * Info:
 **/

@FeignClient("guli-coupon")
public interface CouponFeignClient {
}
