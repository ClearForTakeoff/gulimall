package com.gulimall.guliorder.controller.web;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.exception.NoStockException;
import com.gulimall.guliorder.entity.OrderEntity;
import com.gulimall.guliorder.entity.OrderItemEntity;
import com.gulimall.guliorder.entity.vo.OrderConfirmVo;
import com.gulimall.guliorder.entity.vo.OrderSubmitVo;
import com.gulimall.guliorder.entity.vo.PayVo;
import com.gulimall.guliorder.entity.vo.SubmitOrderResponseVo;
import com.gulimall.guliorder.service.OrderItemService;
import com.gulimall.guliorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/
@Controller
public class WebController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderItemService orderItemService;
    @GetMapping("/{page}.html")
    public String ToPage(@PathVariable("page") String page){
        return page;
    }

    //跳转到订单确认页
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        //展示订单的数据
        //获取到订单所需的信息
        OrderConfirmVo orderConfirmVo = orderService.getOrderInfo();
        model.addAttribute("orderInfo",orderConfirmVo);
        return "confirm";
    }

    //提交订单的请求
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes attributes){
        //System.out.println("提交的数据："+orderSubmitVo.toString());
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);
            //下单成功来到支付选择页
            //下单失败回到订单确认页重新确定订单信息
            if (responseVo.getCode() == 0) {
                //成功
                model.addAttribute("order",responseVo.getOrder());
                return "pay";
            } else {
                String msg = "下单失败";
                switch (responseVo.getCode()) {
                    case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                    case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                    case 3: msg += "库存锁定失败，商品库存不足"; break;
                }
                attributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String message = ((NoStockException)e).getMessage();
                attributes.addFlashAttribute("msg",message);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }



    @RequestMapping("/pay")
    public String payOrder(@RequestParam("orderSn") String orderSn, Model model)  {
        PayVo payVo = new PayVo();
        //查询订单信息
        OrderEntity orderStatus = orderService.getOrderStatus(orderSn);
        payVo.setTotal_amount(orderStatus.getPayAmount().setScale(2, BigDecimal.ROUND_CEILING).toString());
        payVo.setOut_trade_no(orderSn);
        //取到订单项
        OrderItemEntity orderItemEntity = orderItemService.getOne(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        payVo.setSubject(orderItemEntity.getSkuName());
        model.addAttribute("pay",payVo);
        return "Alipay";
    }
}
