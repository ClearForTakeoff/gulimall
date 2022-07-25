package com.gulimall.guliseckill.controller;

import com.common.utils.R;
import com.gulimall.guliseckill.service.SeckillService;
import com.gulimall.guliseckill.to.SeckillSkuRedisTo;
import com.gulimall.guliseckill.vo.SeckillSkuRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/15
 * @Description:
 **/
@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;
    //查询出参与秒杀的商品
    @GetMapping("/currentSeckillSkus")
    @ResponseBody
    public R getSeckillSkus(){
        List< SeckillSkuRedisTo> list = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(list);
    }

    //查询商品的秒杀信息
    @GetMapping( "/skuSeckillInfo/{skuId}")
    @ResponseBody
    public R getSkuSecKill(@PathVariable("skuId")Long skuId){
        SeckillSkuRedisTo redisTo = seckillService.getSkuSeckill(skuId);
        return R.ok().setData(redisTo);
    }

    //秒杀商品
    @GetMapping("kill")
    public String kill(@RequestParam("killId")String killId,
                       @RequestParam("key")String key,
                       @RequestParam("num")Integer num,
                       Model model){
        //判断是否登录
        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("order",orderSn);
        return "success";
    }

}
