package com.gulimall.guliorder.interceptor;

import com.common.to.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.common.constant.AuthConstant.LOGIN_USER;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();
    //拦截器放行前，判断用户是否都登陆
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/guliorder/order/orderStatus/**", uri);
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        if (match || match1) {
            return true;
        }

        HttpSession session = request.getSession();
        //获取登录对象的session
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        if(attribute != null){ //已登录，放行到，页面
            threadLocal.set(attribute); //放入共享数据
            return true;
        }else{
            //未登录
            //重定向到登录页面
            response.sendRedirect("http://auth.gulimall.com/login.html");
        }
        return false;
    }
}
