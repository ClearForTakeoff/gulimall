package com.gulimall.gulicart.interceptor;

import com.common.to.MemberResponseVo;
import com.gulimall.gulicart.vo.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.UUID;

import static com.common.constant.AuthConstant.LOGIN_USER;
import static com.common.constant.CartConstant.TEMP_USER_KEY;
import static com.common.constant.CartConstant.TEMP_USER_KEY_MAX_LIVE;


/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description: 拦截器，获取用户的登陆状态已经临时cookie的id
 **/
@Component
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();
    //拦截器放行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserInfo userInfo = new UserInfo();
        //获取登录信息
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        //已经登录了
        if(attribute != null){
            userInfo.setUserId(attribute.getId());
        }
        //获取cookie
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            //名为 user-key的cookie
            String name = cookie.getName();
            if(name.equals(TEMP_USER_KEY)){
                userInfo.setUserKey(cookie.getValue());
                userInfo.setTempUser(false); //设置为false表示cookie已存在
            }
        }
        //没有user-key分配一个
        if(StringUtils.isEmpty(userInfo.getUserKey())){
            String s = UUID.randomUUID().toString();
            userInfo.setUserKey(s);
            userInfo.setTempUser(true); //没有cookie
        }
        //把用户信息放进去
        threadLocal.set(userInfo);
        return true;
    }

    //拦截器放行业务，执行之后
    //把user-key放到cookie
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //获取当前用户的值
        UserInfo userInfo = threadLocal.get();
        //如果是第一次浏览器访问，没有cookie才创建，有的话就不创建了
        if(userInfo.isTempUser()){
            //创建一个cookie
            Cookie cookie = new Cookie(TEMP_USER_KEY, userInfo.getUserKey());
            //扩大作用域
            cookie.setDomain("gulimall.com");
            //设置过期时间
            cookie.setMaxAge(TEMP_USER_KEY_MAX_LIVE); //过期时间30天
            response.addCookie(cookie);
        }

    }
}
