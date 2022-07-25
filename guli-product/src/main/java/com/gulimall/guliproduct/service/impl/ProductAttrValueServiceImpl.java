package com.gulimall.guliproduct.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.ProductAttrValueDao;
import com.gulimall.guliproduct.entity.ProductAttrValueEntity;
import com.gulimall.guliproduct.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    //查询spu的规格参数属性值
    @Override
    public List<ProductAttrValueEntity> listSpuAttrValue(Long spuId) {
        List<ProductAttrValueEntity> spu_id = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return spu_id;
    }

//更新spu的属性
    @Override
    public void updateSpu(Long spuId, List<ProductAttrValueEntity> entities) {
        List<ProductAttrValueEntity> collect = entities.stream().map(item -> {

            Long attrId = item.getAttrId();
            QueryWrapper<ProductAttrValueEntity> productAttrValueEntityQueryWrapper = new QueryWrapper<>();
            //根据spuid和attrid查到数据库的数据
            productAttrValueEntityQueryWrapper.eq("spu_id", spuId).eq("attr_id", attrId);
            ProductAttrValueEntity one = this.getOne(productAttrValueEntityQueryWrapper);
            //进行数据更新
            one.setAttrValue(item.getAttrValue());
            one.setQuickShow(item.getQuickShow());
            return one;
        }).collect(Collectors.toList());
        //更新数据
        this.updateBatchById(collect);
    }



}