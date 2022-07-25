package com.gulimall.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.to.SocialUser;
import com.common.to.UserLoginVo;
import com.common.utils.PageUtils;
import com.gulimall.gulimember.entity.MemberEntity;
import com.gulimall.gulimember.vo.UserRegistVo;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * 会员
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:48:51
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //注册会员
    void registerMember(UserRegistVo userRegistVo);

    //验证登录
    MemberEntity  verifyLogin(UserLoginVo userLoginVo);

    //用户社交登录
    MemberEntity login(SocialUser socialUser) throws Exception;
}

