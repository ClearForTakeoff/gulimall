package com.gulimall.guliproduct.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.SkuImagesDao;
import com.gulimall.guliproduct.entity.SkuImagesEntity;
import com.gulimall.guliproduct.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    //根据skuid查询所有的图片
    @Override
    public List<SkuImagesEntity> listBySkuId(Long skuId) {
        QueryWrapper<SkuImagesEntity> skuImagesEntityQueryWrapper = new QueryWrapper<>();
        skuImagesEntityQueryWrapper.eq("sku_id",skuId);
        List<SkuImagesEntity> list = this.list(skuImagesEntityQueryWrapper);
        return list;
    }

}