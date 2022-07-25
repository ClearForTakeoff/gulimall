package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatch(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
