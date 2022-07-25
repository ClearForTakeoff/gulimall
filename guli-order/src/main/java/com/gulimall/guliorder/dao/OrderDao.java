package com.gulimall.guliorder.dao;

import com.gulimall.guliorder.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-06 00:03:45
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
