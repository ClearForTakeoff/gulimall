package com.gulimall.guliproduct.feign;

import com.common.to.SkuReductionTo;
import com.common.to.SpuBonusTo;
import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author:admin
 * date:2022/6/2
 * Info:
 *  远程调用的方法中的对象类型与调用的目标方法中类型的属性名一致就行，不需要类型一致
 **/

@FeignClient("guli-coupon")
public interface CouponFeignClient {


    @PostMapping("/gulicoupon/skufullreduction/saveFullReduction")
    R saveSkuReduction(SkuReductionTo skuReductionTo);

    @RequestMapping("/gulicoupon/spubounds/save")
    //@RequiresPermissions("gulicoupon:spubounds:save")
    public R save(@RequestBody SpuBonusTo spuBounds);
}
