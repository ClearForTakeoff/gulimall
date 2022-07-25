package com.gulimall.gulicoupon.dao;

import com.gulimall.gulicoupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:45:14
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
