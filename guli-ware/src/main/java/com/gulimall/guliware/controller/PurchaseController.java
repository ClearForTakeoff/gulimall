package com.gulimall.guliware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.gulimall.guliware.vo.MergeVo;
import com.gulimall.guliware.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliware.entity.PurchaseEntity;
import com.gulimall.guliware.service.PurchaseService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 采购信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:50:27
 */
@RestController
@RequestMapping("guliware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;




    /**
     * @MethodName: finishPurchase
     * @Param:
     * @Return:
     * @Date: 2022-06-08
     * @Description : 完成采购单后回传的数据更新到数据库
    **/
    @RequestMapping("/done")
    public R finishPurchase(@RequestBody PurchaseFinishVo purchaseFinishVo){
        purchaseService.finishPurchase(purchaseFinishVo);
        return R.ok();
    }
    /**
     * @MethodName: receive
     * @Param:
     * @Return:
     * @Date: 2022-06-07
     * @Description : 领取采购单的额方法
    **/
    @RequestMapping("/receive")
    public R receive(@RequestBody List<Long> purchaseIds){
        purchaseService.receive(purchaseIds);
        return R.ok();
    }
    /**
     * param:
     * return:
     * description: 合并采购需求为一个采购单
    */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }
    /**
     * param:
     * return:
     * description: 查询未领取的采购单
    */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("guliware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
