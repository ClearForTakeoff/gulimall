package com.gulimall.guliorder.controller.web;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.utils.R;

import com.gulimall.guliorder.entity.OrderEntity;
import com.gulimall.guliorder.entity.OrderItemEntity;
import com.gulimall.guliorder.entity.vo.PayVo;
import com.gulimall.guliorder.service.OrderItemService;
import com.gulimall.guliorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: duhang
 * @Date: 2022/7/14
 * @Description:
 **/
@Controller
public class PayController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    //模拟支付成功的请求
    @GetMapping("/payed")
    public String payFinish(@RequestParam("orderSn")String orderSn,@RequestParam("subject")String paymentSubject){
        orderService.payFinish(orderSn,paymentSubject);
        return "redirect:http://member.gulimall.com/memberOrders.html";
    }

}
