package com.gulimall.guliproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gulimall.guliproduct.dao.BrandDao;
import com.gulimall.guliproduct.dao.CategoryDao;
import com.gulimall.guliproduct.entity.BrandEntity;
import com.gulimall.guliproduct.entity.BrandFrontVo;
import com.gulimall.guliproduct.entity.CategoryEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.CategoryBrandRelationDao;
import com.gulimall.guliproduct.entity.CategoryBrandRelationEntity;
import com.gulimall.guliproduct.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    //注入品牌的dao
    @Autowired
    BrandDao brandDao;
    //分类的dao
    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //根据品牌id查询到所属的分类

        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveCategory(CategoryBrandRelationEntity categoryBrandRelation) {
        //拿到品牌id和分类id,得到品牌名和分类名
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        //1.查询品牌名
        BrandEntity brandEntity = brandDao.selectById(brandId);
        //得到品牌名
        String brandEntityName = brandEntity.getName();

        //2.查询分类名
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        String categoryEntityName = categoryEntity.getName();

        categoryBrandRelation.setBrandName(brandEntityName);
        categoryBrandRelation.setCatelogName(categoryEntityName);
        //保存记录
        baseMapper.insert(categoryBrandRelation);

    }

    @Override
    public void updateBrand(Long brandId, String name) {
        //1.新建更新的对象数据
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        //2.查询条件对象
        UpdateWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityUpdateWrapper = new UpdateWrapper<>();
        categoryBrandRelationEntityUpdateWrapper.eq("brand_id",brandId);
        //3.更新
        baseMapper.update(categoryBrandRelationEntity,categoryBrandRelationEntityUpdateWrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        //1.新建更新数据对象
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(catId);
        categoryBrandRelationEntity.setCatelogName(name);
        //2.新建更新条件对象
        UpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("catelog_id",catId);
        //3.更细数据
        baseMapper.update(categoryBrandRelationEntity,updateWrapper);
    }

    //根据分类id查询品牌
    @Override
    public List<BrandEntity> getBrandByCategory(Long catId) {

        //新建查询对象
        QueryWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityQueryWrapper = new QueryWrapper<>();
        categoryBrandRelationEntityQueryWrapper.eq("catelog_id",catId);
        //查询到对象
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = baseMapper.selectList(categoryBrandRelationEntityQueryWrapper);
        //得到brandId
        List<Long> brandIds = categoryBrandRelationEntities.stream().map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
        //获得brand对象
        List<BrandEntity> brandEntities = brandDao.selectBatchIds(brandIds);
        return brandEntities;
    }

}