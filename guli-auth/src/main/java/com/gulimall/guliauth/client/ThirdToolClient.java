package com.gulimall.guliauth.client;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description:
 **/
@FeignClient("third-tool") //调用的服务名
public interface ThirdToolClient {
    @GetMapping("/send/{phoneNumber}") //请求的全路径
     R sendMessage(@PathVariable("phoneNumber") String phoneNumber);
}
