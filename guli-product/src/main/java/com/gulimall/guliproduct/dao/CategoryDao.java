package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
