package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查询spu的属性
    List<ProductAttrValueEntity> listSpuAttrValue(Long spuId);

    //更新spu'属性
    void updateSpu(Long skuId, List<ProductAttrValueEntity> entities);



}

