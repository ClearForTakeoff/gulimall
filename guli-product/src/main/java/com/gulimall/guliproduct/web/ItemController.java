package com.gulimall.guliproduct.web;

import com.gulimall.guliproduct.entity.SkuInfoEntity;
import com.gulimall.guliproduct.service.SkuInfoService;
import com.gulimall.guliproduct.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Author: duhang
 * @Date: 2022/6/27
 * @Description:
 **/
@Controller
public class ItemController {

    //查询sku详情信息
    @Autowired
    SkuInfoService skuInfoService;


    //根据skuid获取商品详情
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItem = skuInfoService.getSkuItem(skuId);
        model.addAttribute("item",skuItem);
        return "item";
    }
}
