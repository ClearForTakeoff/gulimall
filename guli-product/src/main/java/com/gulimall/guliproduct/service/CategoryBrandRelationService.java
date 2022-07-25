package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.BrandEntity;
import com.gulimall.guliproduct.entity.BrandFrontVo;
import com.gulimall.guliproduct.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveCategory(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    //根据分类id查询品牌
    List<BrandEntity> getBrandByCategory(Long params);
}

