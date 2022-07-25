package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gulimall.guliproduct.vo.SkuItemAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    //根据spuid获取所有的销售属性
    List<SkuItemAttrVo> getBaseAttrGroup(@Param("spuId") Long spuId);

    //根据skuid获取销售属性值
    List<String> selectAttrListBySkuId(@Param("skuId")Long skuId);
}
