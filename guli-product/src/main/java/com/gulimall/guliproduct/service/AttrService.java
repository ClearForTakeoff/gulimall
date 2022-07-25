package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.AttrEntity;
import com.gulimall.guliproduct.vo.AttrRespVo;
import com.gulimall.guliproduct.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, String type, Long catId);

    //保存属性
    void saveAttr(AttrVo attr);

    //查询属性信息
    AttrRespVo getAttrInfo(Long attrId);

    //更新属性信息
    void updateAttr(AttrVo attr);

    //筛选出可被检索的属性id
    List<Long> getSearchAttrIds(List<Long> attrIds);
}

