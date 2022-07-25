package com.gulimall.guliproduct.service.impl;

import com.gulimall.guliproduct.dao.CategoryBrandRelationDao;
import com.gulimall.guliproduct.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.BrandDao;
import com.gulimall.guliproduct.entity.BrandEntity;
import com.gulimall.guliproduct.service.BrandService;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    //注入品牌分类关联表
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();

        //带查询条件的查询，params中带key
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){
            //将key加入到查询条件,key可以作为id和name
            queryWrapper.eq("brand_id",key).or().like("name",key);

        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateRelatedTable(BrandEntity brand) {
        //1.先更新品牌表数据
        baseMapper.updateById(brand);
        //2.如果品牌名有变化，则更新品牌分类关联表的数据
        if(!StringUtils.isEmpty(brand.getName())){
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
            //TODO 更新其他关联表
        }
    }

}