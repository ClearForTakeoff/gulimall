package com.gulimall.guliware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.common.to.HasStockTo;
import com.gulimall.guliware.exception.NoStockException;
import com.gulimall.guliware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliware.entity.WareSkuEntity;
import com.gulimall.guliware.service.WareSkuService;
import com.common.utils.PageUtils;
import com.common.utils.R;

import static com.common.exception.BizCodeEnum.NO_STOCK_EXCEPTION;


/**
 * 商品库存
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:50:27
 */
@RestController
@RequestMapping("guliware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    //锁定商品库存
    @PostMapping(value = "/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo) {
        try {
            boolean lockStock = wareSkuService.orderLockStock(vo);
            return R.ok().setData(lockStock);
        } catch (NoStockException e) {
            return R.error(NO_STOCK_EXCEPTION.getCode(),NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * @MethodName: hasStock
     * @Param:
     * @Return:
     * @Date: 2022-06-14
     * @Description :查询商品是否有库存
    **/
    @RequestMapping("/hasStock")
    public R hasStock(@RequestBody List<Long> skuIds){

        List<HasStockTo> skuHasStock = wareSkuService.getSkuHasStock(skuIds);

        R r = R.ok().setData(skuHasStock);
        return r;
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
