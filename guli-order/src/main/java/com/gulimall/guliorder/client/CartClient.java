package com.gulimall.guliorder.client;

import com.gulimall.guliorder.entity.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
@FeignClient("guli-cart")
public interface CartClient {
    @GetMapping("/getCheckedCartItem")
    public List<OrderItemVo> getCheckedCartItem();
}
