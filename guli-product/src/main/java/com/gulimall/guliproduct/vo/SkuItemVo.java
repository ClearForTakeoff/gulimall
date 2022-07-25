package com.gulimall.guliproduct.vo;

import com.gulimall.guliproduct.entity.SkuImagesEntity;
import com.gulimall.guliproduct.entity.SkuInfoEntity;
import com.gulimall.guliproduct.entity.SpuInfoDescEntity;
import com.gulimall.guliproduct.entity.SpuInfoEntity;
import com.gulimall.guliproduct.to.SeckillSkuTo;
import lombok.Data;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/27
 * @Description: 前端商品详情页面的vo
 **/

@Data
public class SkuItemVo {
    //sku的基本信息
    private SkuInfoEntity skuInfoEntity;
    //sku图片信息
    private List<SkuImagesEntity> skuImagesEntities;
    //sku的销售属性组合
    private List<SkuItemAttrVo> attrVos;

    //spu介绍信息
    private SpuInfoDescEntity spuInfoDescEntity;

    //有无货
    private boolean hasStock;
    //spu的基本属性
    private List<SpuBaseAttrGroupVo> spuItemBaseAttrVos;

    @Data
    public static class SpuBaseAttrGroupVo{
        private String  attrGroupName;
        private List<SpuBaseAttrVo> baseAttrVos;
    }

    @Data
    public static class SpuBaseAttrVo{
        private String attrName;
        private String attrValue;
    }

    //商品秒杀信息
    private SeckillSkuTo seckillSkuTo;
}
