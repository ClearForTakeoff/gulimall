package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliproduct.entity.CategoryEntity;
import com.gulimall.guliproduct.service.CategoryService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 商品三级分类
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@RestController
@RequestMapping("guliproduct/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表,查询所有分类及其子分类，组成树形结构
     */
    @RequestMapping("/list/tree")
    //@RequiresPermissions("guliproduct:category:list")
    public R list(@RequestParam Map<String, Object> params){
        //在service中实现查询到所有分类的树形结构
        List<CategoryEntity> listTree = categoryService.listAsTree();

        return R.ok().put("data",listTree);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("guliproduct:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }


    /**
     * 修改
     * 需要更新关联表的数据
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateRelatedTable(category);

        return R.ok();
    }

    @RequestMapping("/update/sort")
    //@RequiresPermissions("guliproduct:category:update")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 删除
     * 必须发送post请求
     */
    @RequestMapping(value = "/delete")
    //@RequiresPermissions("guliproduct:category:delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds();
        //自定义删除方法
        categoryService.removeMenuByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
