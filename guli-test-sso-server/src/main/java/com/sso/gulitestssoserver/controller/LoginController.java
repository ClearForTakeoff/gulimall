package com.sso.gulitestssoserver.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author: duhang
 * @Date: 2022/7/3
 * @Description:
 **/
@Controller
public class LoginController {

    //redis
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //接受所有登录请求
    @GetMapping("/")
    public String loginPage(@RequestParam(value = "clienturl",required = false) String clienturl, Model model,
                            @CookieValue(value = "sso_token",required = false)String cookieValue){
        //如果能得到cookie，说明有人登录了,直接跳转回客户端
        if(!StringUtils.isEmpty(cookieValue)){
            //跳转回客户端,带上令牌
            return "redirect:"+clienturl + "?token="+cookieValue;
        }
        //请求得客户端地址放到请求域中
        model.addAttribute("clienturl",clienturl);
        return "login";
    }

    //登录成功,
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("name") String name, @RequestParam("password")String password,
                          @RequestParam(value = "clienturl",required = false)String clienturl,
                          HttpServletResponse response){
        //拿到请求地址中的客户端地址
        //只要有输入，就认为登陆成功
        if(!StringUtils.isEmpty(name) &&!StringUtils.isEmpty(password)){
            //redis
            String replace = UUID.randomUUID().toString().replace("-", "");
            stringRedisTemplate.opsForValue().set(name,replace);
            //浏览器放cookie
            Cookie sso_token = new Cookie("sso_token", replace);
            response.addCookie(sso_token);
            //跳转回客户端,带上令牌
            return "redirect:"+clienturl + "?token="+replace;
        }else{
            //登陆失败
            return "login";
        }

    }
}
