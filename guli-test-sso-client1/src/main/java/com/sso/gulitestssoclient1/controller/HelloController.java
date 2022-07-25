package com.sso.gulitestssoclient1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * @Author: duhang
 * @Date: 2022/7/3
 * @Description:
 **/
@Controller
public class HelloController {

    //无需登录的请求
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello--client--1";
    }

    //需要登录的请求
    @GetMapping("/needLogin")
    public String selectData(Model model, HttpSession httpSession, @RequestParam(value = "token" ,required = false)String token){
        //路径上有token就表示登陆成功
        if(!StringUtils.isEmpty(token)){
            httpSession.setAttribute("user",token);
        }
        //判断是否登录
        Object user = httpSession.getAttribute("user");
        if(user == null){
            //没登录,跳转到登录服务器,加上当前访问登录服务器的地址
            return "redirect:http://ssoserver.com:8770"+"?clienturl=http://ssoclient1.com:8777/needLogin";
        }
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if(i % 10 == 2){
                strings.add("data ---" + i);
            }
            strings.add("data =====" + i);
        }
        model.addAttribute("data",strings);
        return "data";
    }

}
