package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliproduct.entity.SkuSaleAttrValueEntity;
import com.gulimall.guliproduct.service.SkuSaleAttrValueService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * sku销售属性&值
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
@RestController
@RequestMapping("guliproduct/skusaleattrvalue")
public class SkuSaleAttrValueController {
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;


    @GetMapping("/getSkuAttrStrList/{skuId}")
    public List<String> getSkuSaleAttr(@PathVariable("skuId") Long skuId){
        List<String> attrStrList = skuSaleAttrValueService.selectAttrList(skuId);
        return attrStrList;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:skusaleattrvalue:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuSaleAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliproduct:skusaleattrvalue:info")
    public R info(@PathVariable("id") Long id){
		SkuSaleAttrValueEntity skuSaleAttrValue = skuSaleAttrValueService.getById(id);

        return R.ok().put("skuSaleAttrValue", skuSaleAttrValue);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:skusaleattrvalue:save")
    public R save(@RequestBody SkuSaleAttrValueEntity skuSaleAttrValue){
		skuSaleAttrValueService.save(skuSaleAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:skusaleattrvalue:update")
    public R update(@RequestBody SkuSaleAttrValueEntity skuSaleAttrValue){
		skuSaleAttrValueService.updateById(skuSaleAttrValue);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:skusaleattrvalue:delete")
    public R delete(@RequestBody Long[] ids){
		skuSaleAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
