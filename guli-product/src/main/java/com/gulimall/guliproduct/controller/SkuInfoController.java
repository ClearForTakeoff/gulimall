package com.gulimall.guliproduct.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gulimall.guliproduct.entity.SkuInfoEntity;
import com.gulimall.guliproduct.service.SkuInfoService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * sku信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
@RestController
@RequestMapping("guliproduct/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    //查询sku价格
    @RequestMapping("/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId")Long skuId){
        return skuInfoService.getSkuPrice(skuId);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
       // PageUtils page = skuInfoService.queryPage(params);
        PageUtils page = skuInfoService.queryPageCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //@RequiresPermissions("guliproduct:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
