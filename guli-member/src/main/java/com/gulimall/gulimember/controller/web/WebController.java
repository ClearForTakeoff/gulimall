package com.gulimall.gulimember.controller.web;

import com.alibaba.fastjson.JSON;
import com.common.utils.R;
import com.gulimall.gulimember.feign.OrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: duhang
 * @Date: 2022/7/14
 * @Description:
 **/
@Controller
public class WebController {

    @Autowired
    OrderClient orderClient;
    @GetMapping("/memberOrders.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",required = false,defaultValue = "0") Integer pageNum,
                                  Model model, HttpServletRequest request) {

        //获取到支付宝给我们转来的所有请求数据
        //request,验证签名


        //查出当前登录用户的所有订单列表数据
        Map<String,Object> page;
        page = new HashMap<>();
        page.put("page",pageNum.toString());

        //远程查询订单服务订单数据
        R orderInfo = orderClient.getOrderList(page);
        System.out.println(JSON.toJSONString(orderInfo));
        model.addAttribute("orders",orderInfo);

        return "list";
    }
}
