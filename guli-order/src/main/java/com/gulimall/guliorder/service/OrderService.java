package com.gulimall.guliorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.to.SeckillOrderTo;
import com.common.utils.PageUtils;
import com.gulimall.guliorder.entity.OrderEntity;
import com.gulimall.guliorder.entity.vo.OrderConfirmVo;
import com.gulimall.guliorder.entity.vo.OrderSubmitVo;
import com.gulimall.guliorder.entity.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-06 00:03:45
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //订单确认页信息
    OrderConfirmVo getOrderInfo() throws ExecutionException, InterruptedException;

    //提交订单
    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    //查询订单状态
    OrderEntity getOrderStatus(String orderSn);

    //关闭订单
    void closeOrder(OrderEntity orderEntity);

    PageUtils getOrderList(Map<String,Object> params);

    //支付成功
    void payFinish(String orderSn,String paymentSubject);

    //创建秒杀订单
    void createSeckillOrder(SeckillOrderTo seckillOrderTo);
}

