package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.SkuSaleAttrValueEntity;
import com.gulimall.guliproduct.vo.SkuItemAttrVo;
import com.gulimall.guliproduct.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //获取sku销售属性分组的信息
    List<SkuItemAttrVo> getSaleAttrGroup(Long spuId);

    //获取sku的属性值
    List<String> selectAttrList(Long skuId);
}

