package com.gulimall.guliproduct.dao;

import com.gulimall.guliproduct.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:50
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
