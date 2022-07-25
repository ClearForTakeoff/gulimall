package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gulimall.guliproduct.entity.BrandEntity;
import com.gulimall.guliproduct.entity.BrandFrontVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliproduct.entity.CategoryBrandRelationEntity;
import com.gulimall.guliproduct.service.CategoryBrandRelationService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@RestController
@RequestMapping("guliproduct/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    //查询商品分类关联的品牌
    @RequestMapping("/brands/list")
    public R getCategoryRelationBrand(@RequestParam(value = "catId") Long params){
        //params带品牌id
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandByCategory(params);
        //把BrandEntity转为前端需要的
        List<Object> data = brandEntities.stream().map((brandEntity -> {
            BrandFrontVo brandFrontVo = new BrandFrontVo();
            brandFrontVo.setBrandId(brandEntity.getBrandId());
            brandFrontVo.setBrandName(brandEntity.getName());
            return brandFrontVo;
        })).collect(Collectors.toList());
        return R.ok().put("data",data);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    //查询品牌关联的商品分类列表
    //根绝品牌id，查询到商品分类
    @GetMapping("/catelog/list")
    public R categoryBrandRelationshipList(@RequestParam("brandId") Long brandId){

        //根据传参的id查询到id的分类
        List<CategoryBrandRelationEntity> res = categoryBrandRelationService
                .list(
                        new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
                );
        return R.ok().put("data", res);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliproduct:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveCategory(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
