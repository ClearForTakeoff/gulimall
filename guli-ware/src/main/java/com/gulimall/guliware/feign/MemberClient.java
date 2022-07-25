package com.gulimall.guliware.feign;

import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: duhang
 * @Date: 2022/7/8
 * @Description:
 **/
@FeignClient("guli-member")
public interface MemberClient {
    /**
     * 根据id获取用户地址信息
     * @param id
     * @return
     */
    @RequestMapping("/gulimember/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);

}
