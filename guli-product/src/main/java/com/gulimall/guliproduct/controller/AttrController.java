package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.gulimall.guliproduct.entity.ProductAttrValueEntity;
import com.gulimall.guliproduct.service.ProductAttrValueService;
import com.gulimall.guliproduct.vo.AttrRespVo;
import com.gulimall.guliproduct.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gulimall.guliproduct.entity.AttrEntity;
import com.gulimall.guliproduct.service.AttrService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 商品属性
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@RestController
@RequestMapping("/guliproduct/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @RequestMapping("/update/{spuId}")
    public R updateSpuAttrValue(@PathVariable("spuId") Long skuId,@RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpu(skuId,entities);
        return R.ok();
    }

    /**
     * @MethodName: listSpuAttrValue
     * @Param:
     * @Return:
     * @Date: 2022-06-08
     * @Description : 根据spuid查询属性信息.用于回显数据
    **/
    @RequestMapping("/base/listforspu/{spuId}")
    public R listSpuAttrValue(@PathVariable("spuId")Long spuId){
        List<ProductAttrValueEntity> data = productAttrValueService.listSpuAttrValue(spuId);
        return R.ok().put("data",data);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);
        return R.ok().put("page", page);
    }

    @RequestMapping("/{type}/list/{catId}")
    //@RequiresPermissions("guliproduct:attr:list")
    public R baseList(@RequestParam Map<String, Object> params,@PathVariable("type") String type,@PathVariable("catId") Long catId){
        PageUtils page = attrService.queryPage(params,type,catId);

        return R.ok().put("page", page);
    }

    /**
     * 信息,根据属性id查询到属性的前端显示信息，用于修改页面的回显
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("guliproduct:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:attr:update")
    public R update(@RequestBody AttrVo attr){
		//attrService.updateById(attr);
        attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
