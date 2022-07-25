package com.gulimall.guliproduct.web;

import com.gulimall.guliproduct.entity.CategoryEntity;
import com.gulimall.guliproduct.service.CategoryService;
import com.gulimall.guliproduct.vo.front.TwoCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: duhang
 * @Date: 2022/6/15
 * @Description:
 **/
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index","/index.html"})
    public String index(Model model){
        //查询商品分类
        List<CategoryEntity> categoryEntityList = categoryService.getOneLevelCategory();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    //获取三级分类数据
    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String,List<TwoCategoryVo>> getCategoryList(){
        Map<String,List<TwoCategoryVo>> map = categoryService.getCategoryJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
