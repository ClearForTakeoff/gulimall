package com.gulimall.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.gulimall.guliproduct.entity.AttrAttrgroupRelationEntity;
import com.gulimall.guliproduct.entity.AttrEntity;
import com.gulimall.guliproduct.entity.AttrGroupAttrFrontVo;
import com.gulimall.guliproduct.service.AttrAttrgroupRelationService;
import com.gulimall.guliproduct.service.CategoryService;
import com.gulimall.guliproduct.vo.AttrAttrRelationVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.guliproduct.entity.AttrGroupEntity;
import com.gulimall.guliproduct.service.AttrGroupService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 属性分组
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@RestController
@RequestMapping("guliproduct/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private CategoryService categoryService;


    //根据商品分类获取属性分组及其属性
    @RequestMapping("/{catId}/withattr")
    public R getAttrGroupByCatId(@PathVariable("catId")Long catId){
        //获取分类下的所有属性分组
        //获取所有属性分组下的属性
        List<AttrGroupAttrFrontVo> data = attrGroupService.getAttrGroupByCatId(catId);
        return R.ok().put("data",data);
    }
    /**
     * param:
     * return:
     * description: 新增关联关系
    */
    @PostMapping("/attr/relation")
    public R addAttrAttrGroupRelation(@RequestBody List<AttrAttrRelationVo> attrAttrRelationVo){
        //list转换
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrRelationVo.stream().map((item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        })).collect(Collectors.toList());
        attrAttrgroupRelationService.saveBatch(relationEntities);
        return R.ok();
    }
    /**
     * param:
     * return:
     * description: 分页查询没有关联到属性分组的属性
    */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R getNoRelationAttr(@RequestParam Map<String, Object> params ,@PathVariable Long attrGroupId){
        PageUtils page =  attrGroupService.getNoAttrRelation(params,attrGroupId);
        return R.ok().put("page",page);
    }
    /**
     * param:
     * return:
     * description: 删除属性分组与属性之间的关联关系
    */
    //attr/relation/delete
    @RequestMapping("/attr/relation/delete")
    public R deleteAttrAttrGroupRelation(@RequestBody AttrAttrRelationVo[] attrAttrRelationVos){
        attrGroupService.deleteRelation(attrAttrRelationVos);
        return R.ok();
    }

    /**
     * param: 属性分组的id
     * return:
     * description: 根据属性分组的id查询到属性分组关联到的商品属性
    */
    @RequestMapping("/{attrGroupId}/attr/relation")
    public R getGroupRelationship(@PathVariable("attrGroupId" )Long attrGroupId){

        List<AttrEntity> res = attrGroupService.getAttrGroupRelation(attrGroupId);
        return R.ok().put("data",res);
    }



    /**
     * 列表
     * categoryId:前端传入的三级分类的id
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("guliproduct:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable Long categoryId){
        PageUtils page = attrGroupService.queryPage(params,categoryId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("guliproduct:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        //得到属性的分类id
        Long catelogId = attrGroup.getCatelogId();
        //根据属性的分类id得到三级分类的id
        Long[] categoryIds = categoryService.getCatelogPath(catelogId);
        attrGroup.setCatelogPath(categoryIds);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
