package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.Map;


import com.common.validator.group.AddGroup;
import com.common.validator.group.UpdateGroup;
import com.common.validator.group.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gulimall.guliproduct.entity.BrandEntity;
import com.gulimall.guliproduct.service.BrandService;
import com.common.utils.PageUtils;
import com.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@RestController
@RequestMapping("guliproduct/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("guliproduct:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:brand:save")
    //开启校验的注解
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
		brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:brand:update")
    public R update(@Validated(value = UpdateGroup.class)@RequestBody BrandEntity brand){
        //修改品牌时要更新关联表的数据
		brandService.updateRelatedTable(brand);

        return R.ok();
    }

    //对状态信息的修改要单独，因为只带一个参数，所以校验不会通过，修改失败,
    //单独创建校验分组，只实现对id和状态的校验
    @RequestMapping("/update/status")
    //@RequiresPermissions("guliproduct:brand:update")
    public R updateStatus(@Validated(UpdateStatus.class)@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
