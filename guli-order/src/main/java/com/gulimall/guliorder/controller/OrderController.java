package com.gulimall.guliorder.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.common.to.MemberResponseVo;
import com.gulimall.guliorder.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliorder.entity.OrderEntity;
import com.gulimall.guliorder.service.OrderService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 订单
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-06 00:03:45
 */
@RestController
@RequestMapping("guliorder/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //查询会员的订单
    @PostMapping("/listOrder")
    public R getOrderList(@RequestBody Map<String, Object> params){
        PageUtils page  =  orderService.getOrderList( params);
        return R.ok().put("page",page);
    }
    //根据订单号，查询订单状态
    @RequestMapping("/orderStatus/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn")String orderSn){
       OrderEntity order = orderService.getOrderStatus(orderSn);
       return R.ok().setData(order);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliorder:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @ResponseBody
    //@RequiresPermissions("guliorder:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliorder:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliorder:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliorder:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
