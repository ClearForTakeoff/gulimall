package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.CategoryEntity;
import com.gulimall.guliproduct.vo.front.TwoCategoryVo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //生成分类的树形结构
    List<CategoryEntity> listAsTree();

    void removeMenuByIds(List<Long> asList);

    //三级分裂id查询所有父分类id
    Long[] getCatelogPath(Long catelogId);

    void updateRelatedTable(CategoryEntity category);

    //查询一级分类
    List<CategoryEntity> getOneLevelCategory();

    //查询所有分类数据，用于封装json给前台页面
    Map<String, List<TwoCategoryVo>> getCategoryJson();
}

