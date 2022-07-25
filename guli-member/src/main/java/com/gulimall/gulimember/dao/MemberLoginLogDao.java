package com.gulimall.gulimember.dao;

import com.gulimall.gulimember.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:48:51
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
