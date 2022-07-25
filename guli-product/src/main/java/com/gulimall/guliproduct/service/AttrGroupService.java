package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.AttrEntity;
import com.gulimall.guliproduct.entity.AttrGroupAttrFrontVo;
import com.gulimall.guliproduct.entity.AttrGroupEntity;
import com.gulimall.guliproduct.vo.AttrAttrRelationVo;
import com.gulimall.guliproduct.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    //查询属性分组下的所有属性
    List<AttrEntity> getAttrGroupRelation(Long attrGroupId);

    //删除属性分组与属性的关联关系
    void deleteRelation(AttrAttrRelationVo[] attrAttrRelationVos);

    //查询到没有关联分组的属性
    PageUtils getNoAttrRelation(Map<String, Object> params, Long attrGroupId);

    //根据商品分类获取属性分组及其属性
    List<AttrGroupAttrFrontVo> getAttrGroupByCatId(Long catId);

    //获取spu的所有分组属性
    List<SkuItemVo.SpuBaseAttrGroupVo> getSKUAttrGroupValueBySpuId(Long spuId,Long catalogId);
}

