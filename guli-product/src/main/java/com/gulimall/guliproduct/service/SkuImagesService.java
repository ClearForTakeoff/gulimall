package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查询所有
    List<SkuImagesEntity> listBySkuId(Long skuId);
}

