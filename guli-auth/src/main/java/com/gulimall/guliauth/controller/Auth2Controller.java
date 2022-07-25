package com.gulimall.guliauth.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


import com.common.to.MemberResponseVo;
import com.common.to.SocialUser;
import com.common.utils.HttpUtils;
import com.common.utils.R;
import com.gulimall.guliauth.client.MemberClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.common.constant.AuthConstant.LOGIN_USER;

/**
 * @Author: duhang
 * @Date: 2022/7/3
 * @Description:
 **/
@Slf4j
@Controller
public class Auth2Controller {

    @Autowired
    private MemberClient memberClient;

    //微博授权成功的请求
    @GetMapping("/auth2/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("client_id","1110211428");
        map.put("client_secret","a50ebf5982dbcfbbbd06e16aa0dee5ae");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/auth2/weibo/success");
        map.put("code",code);

        //1、根据用户授权返回的code换取access_token
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());

        //2、处理
        if (response.getStatusLine().getStatusCode() == 200) {
            //获取到了access_token,转为通用社交登录对象
            String json = EntityUtils.toString(response.getEntity());
            //转为对象，封装登录信息
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            //知道了哪个社交用户
            //1）、当前用户如果是第一次进网站，自动注册进来（为当前社交用户生成一个会员信息，以后这个社交账号就对应指定的会员）
            //登录或者注册这个社交用户
            System.out.println(socialUser.getAccess_token());
            //调用远程服务
            R oauthLogin = memberClient.oauthLogin(socialUser);
            if (oauthLogin.getCode() == 0) {
                MemberResponseVo data = oauthLogin.getDataByName("data", new TypeReference<MemberResponseVo>() {});
                log.info("登录成功：用户信息：{}",data.toString());

                //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie
                //TODO 1、默认发的令牌。当前域（解决子域session共享问题）
                //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
                session.setAttribute(LOGIN_USER,data);

                //2、登录成功跳回首页
                return "redirect:http://gulimall.com";
            } else {

                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}
