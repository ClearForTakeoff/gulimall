package com.gulimall.gulimember.dao;

import com.gulimall.gulimember.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.Pattern;

/**
 * 会员
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:48:51
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    //对手机号进行计数
    int countPhoneNumber(@Param("phoneNumber") String phoneNumber);

    //对用户名进行计数
    int countUsername(@Param("userName") String userName);
}
