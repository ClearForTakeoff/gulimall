package com.gulimall.gulimember.dao;

import com.gulimall.gulimember.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:48:51
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    //获取默认等级
    MemberLevelEntity selectDefaultLevel();
}
