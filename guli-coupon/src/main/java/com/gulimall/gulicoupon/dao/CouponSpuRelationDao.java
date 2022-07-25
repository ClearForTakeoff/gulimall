package com.gulimall.gulicoupon.dao;

import com.gulimall.gulicoupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:45:14
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
