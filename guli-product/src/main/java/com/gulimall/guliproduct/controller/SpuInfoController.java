package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.Map;


import com.common.to.SpuInfoTo;
import com.gulimall.guliproduct.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliproduct.entity.SpuInfoEntity;
import com.gulimall.guliproduct.service.SpuInfoService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * spu信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@RestController
@RequestMapping("guliproduct/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    //根据skuid找到spu信息
    @GetMapping("/getSpuInfoBySkuId/{skuId}")
    public SpuInfoTo getSpuInfoBySkuId(@PathVariable("skuId")Long skuId){
        return spuInfoService.getSpuInfoBySkuId(skuId);
    }

    /**
     * @MethodName: upSpu
     * @Param:
     * @Return:
     * @Date: 2022-06-14
     * @Description : 上架spu
    **/
    @RequestMapping("/{spuId}/up")
    public R upSpu(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = spuInfoService.queryPage(params);
        PageUtils page = spuInfoService.queryPageContidition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliproduct:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:spuinfo:save")
    public R save(@RequestBody SpuSaveVo spuInfo){
		spuInfoService.saveSpuInfo(spuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
