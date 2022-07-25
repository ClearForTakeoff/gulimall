package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updatePublishStatus(@Param("spuId") Long spuId, @Param("status") int status);
}
