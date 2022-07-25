package com.gulimall.guliauth.client;

import com.common.to.SocialUser;
import com.common.to.UserLoginVo;
import com.common.utils.R;
import com.gulimall.guliauth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description:
 **/
@FeignClient("guli-member")
public interface MemberClient {

    //远程服务，账户注册
    @PostMapping("/gulimember/member/registerMember")
     R registerMember(@RequestBody UserRegistVo userRegistVo); //RequestBody表示请求体json数据转为对象

    //远程服务，账户登录
    @PostMapping("/gulimember/member/login")
    R memberLogin(@RequestBody UserLoginVo userLoginVo);


    //社交登录
    @PostMapping("/gulimember/member//oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser);
}
