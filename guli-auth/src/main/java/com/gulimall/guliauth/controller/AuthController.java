package com.gulimall.guliauth.controller;

import com.alibaba.fastjson.TypeReference;
import com.common.to.MemberResponseVo;
import com.common.to.UserLoginVo;
import com.common.utils.R;
import com.gulimall.guliauth.client.MemberClient;
import com.gulimall.guliauth.client.ThirdToolClient;
import com.gulimall.guliauth.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.common.constant.AuthConstant.LOGIN_USER;

/**
 * @Author: duhang
 * @Date: 2022/6/29
 * @Description:
 **/
@Controller
public class AuthController {


    @Autowired
    ThirdToolClient thirdToolClient; //服务调用

    @Autowired
    StringRedisTemplate redisTemplates;

    @Autowired
    MemberClient memberClient;
    //发送短信注册验证码
    @GetMapping("/registerCode")
    @ResponseBody
    public R sendCode(@RequestParam("phoneNumber") String phoneNumber){  //get请求接受参数应该是 requestParam
       return  thirdToolClient.sendMessage(phoneNumber);
    }

    //注册请求
    @PostMapping("/register")
    public String register(@Valid UserRegistVo userRegistVo, BindingResult result, RedirectAttributes model){ //RedirectAttributes重定向视图
        if(result.hasErrors()){ //校验注册
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
            model.addFlashAttribute("errors",collect);
            return "redirect:http://auth.gulimall.com/register.html";
        }
        //后端的参数校验,远程服务
        //先校验验证码
        String redisCode = redisTemplates.opsForValue().get("sms_code:"+userRegistVo.getPhoneNumber());
        if(!StringUtils.isEmpty(redisCode)){
            //分割出redis验证码
            String s = redisCode.split("_")[0];
            if(s.equals(userRegistVo.getCode())){
                //删除验证码,key
                redisTemplates.delete("sms_code:"+userRegistVo.getPhoneNumber());
                //验证码通过，调用远程服务继续校验
                R registRes = memberClient.registerMember(userRegistVo);
                if((Integer)registRes.get("code") == 0){
                    //todo 验证通过，转到登录页面
                    return "redirect:http://auth.gulimall.com/login.html";
                }else{
                    //todo
                    HashMap<String, String> collect = new HashMap<>();
                    //得到错误状态码
                    Integer errorCode = (Integer) registRes.get("code");
                    if(errorCode == 15001){
                        collect.put("userName","用户名已存在");
                    }else if(errorCode == 15002){
                        collect.put("phoneNumber","手机号已存在");
                    }else{
                        collect.put("msg",registRes.getDataByName("msg",new TypeReference<String>(){}));
                    }
                    model.addFlashAttribute("errors",collect);
                    return "redirect:http://auth.gulimall.com/register.html";
                }

            }else{//验证码不正确
                HashMap<String, String> collect = new HashMap<>();
                collect.put("code","验证码输入错误");
                model.addFlashAttribute("errors",collect);
                return "redirect:http://auth.gulimall.com/register.html";
            }
        }else{ //验证码已经过期了
            HashMap<String, String> collect = new HashMap<>();
            collect.put("code","验证码已经失效");
            model.addFlashAttribute("errors",collect);
            return "redirect:http://auth.gulimall.com/register.html";
        }

        //return "redirect:/login.html";
    }


    //登录请求
    @PostMapping("/login")
    public String userLogin(UserLoginVo userLoginVo, RedirectAttributes model, HttpSession httpSession){
        //调用远程服务进行验证
        R r = memberClient.memberLogin(userLoginVo);
        if((Integer)r.get("code") == 0){ //验证成功
            //带cookie
            MemberResponseVo data =  r.getDataByName("data",new TypeReference<MemberResponseVo>(){});
            httpSession.setAttribute(LOGIN_USER,data);
            //验证完成跳转到首页
            return "redirect:http://gulimall.com";
        }else{
            HashMap<String, String> collect = new HashMap<>();
            collect.put("msg",r.getDataByName("msg",new TypeReference<String>(){}));
            model.addFlashAttribute("errors",collect);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    //登录页请求
    @GetMapping("/login.html")
    public String loginPage(HttpSession httpSession){
        //获取登录信息，如果登录了跳转到首页
        Object attribute = httpSession.getAttribute(LOGIN_USER);
        if(attribute == null){ //没有登陆过
            return "login";
        }else{
            //跳转到首页
            return "redirect:http://gulimall.com";
        }

    }


}
