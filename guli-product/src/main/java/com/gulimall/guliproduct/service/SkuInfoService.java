package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.SkuInfoEntity;
import com.gulimall.guliproduct.vo.SkuItemVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //保存sku基本信息
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    //条件查询sku
    PageUtils queryPageCondition(Map<String, Object> params);

    //查询所有的sku
    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    //查询商品详情页的sku信息
    SkuItemVo getSkuItem(Long skuId) throws ExecutionException, InterruptedException;

    //查询sku价格
    BigDecimal getSkuPrice(Long skuId);
}

