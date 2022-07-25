package com.gulimall.guliseckill.client;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 **/
@FeignClient("guli-coupon")
public interface CouponClient {


    @RequestMapping("coupon/seckillsession/listThreeDays")
    public R getLatestThreeDaysSku();
}
