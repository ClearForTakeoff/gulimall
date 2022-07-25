package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    //再attrIds中查询searchType == 1 的id
    List<Long> selectSearchAttrIds(@Param("attrIds")List<Long> attrIds);
}
