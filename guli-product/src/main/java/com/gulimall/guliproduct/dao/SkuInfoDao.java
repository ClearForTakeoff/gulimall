package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * sku信息
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    //查询sku价格
    BigDecimal selectSkuPrice(@Param("skuId") Long skuId);
}
