package com.gulimall.guliproduct.service.impl;

import com.gulimall.guliproduct.vo.SkuItemAttrVo;
import com.gulimall.guliproduct.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.SkuSaleAttrValueDao;
import com.gulimall.guliproduct.entity.SkuSaleAttrValueEntity;
import com.gulimall.guliproduct.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    //获取sku销售属性分组的信息
    @Override
    public List<SkuItemAttrVo> getSaleAttrGroup(Long spuId) {
        List<SkuItemAttrVo> attrs = baseMapper.getBaseAttrGroup(spuId);

        return attrs;
    }

    //获取sku所有属性值
    @Override
    public List<String> selectAttrList(Long skuId) {
        return baseMapper.selectAttrListBySkuId(skuId);
    }

}