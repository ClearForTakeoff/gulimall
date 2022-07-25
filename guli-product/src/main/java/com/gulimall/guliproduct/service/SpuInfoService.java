package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.to.SpuInfoTo;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.SpuInfoEntity;
import com.gulimall.guliproduct.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //保存商品
    void saveSpuInfo(SpuSaveVo spuInfo);

    //保存spu基本信息
    void saveSpuInfoEntity(SpuInfoEntity spuInfo);

    //条件查询所有的spu商品
    PageUtils queryPageContidition(Map<String, Object> params);

    //上架商品
    void up(Long spuId);

    //根据skuid得到spu信息
    SpuInfoTo getSpuInfoBySkuId(Long skuId);
}

