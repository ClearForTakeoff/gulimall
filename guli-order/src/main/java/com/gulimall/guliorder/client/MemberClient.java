package com.gulimall.guliorder.client;

import com.common.utils.R;
import com.gulimall.guliorder.entity.vo.UserAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
@FeignClient("guli-member")
public interface MemberClient {

    //查询会员收货地址
    @RequestMapping("/gulimember/memberreceiveaddress/{memberId}/getMemberAddress}")
    public List<UserAddressVo> getMemberAddress(@PathVariable("memberId") Long memberId);

    //查询会员积分
    @GetMapping("/gulimember/member/getMemberIntegration/{memberId}")
    public Integer getMemberIntegration(@PathVariable("memberId")Long memberId);
}
