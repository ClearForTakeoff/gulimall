package com.gulimall.guliware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


import com.gulimall.guliware.entity.vo.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliware.entity.WareInfoEntity;
import com.gulimall.guliware.service.WareInfoService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 仓库信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:50:27
 */
@RestController
@RequestMapping("guliware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    @GetMapping("/fare")
    public R getFare(@RequestParam("addrId")Long addrId){
        FareVo fare = wareInfoService.getFare(addrId);
        return R.ok().setData(fare);

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliware:wareinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = wareInfoService.queryPage(params);
        //带条件的分页查询
        PageUtils page = wareInfoService.queryPageCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliware:wareinfo:info")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliware:wareinfo:save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliware:wareinfo:update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliware:wareinfo:delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
