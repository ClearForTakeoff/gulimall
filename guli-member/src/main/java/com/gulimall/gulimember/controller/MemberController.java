package com.gulimall.gulimember.controller;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;


import com.common.exception.BizCodeEnum;
import com.common.exception.PhoneNumberExistException;
import com.common.exception.UserNameExistException;
import com.common.to.SocialUser;
import com.common.to.UserLoginVo;
import com.gulimall.gulimember.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gulimall.gulimember.entity.MemberEntity;
import com.gulimall.gulimember.service.MemberService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * 会员
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:48:51
 */
@RestController
@RequestMapping("gulimember/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    //根据会员id查询会员积分
    @GetMapping("/getMemberIntegration/{memberId}")
    public Integer getMemberIntegration(@PathVariable("memberId")Long memberId){
        MemberEntity byId = memberService.getById(memberId);
        return byId.getIntegration();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimember:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimember:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimember:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimember:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimember:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     *  会员注册功能
     *
     */
    @PostMapping("/registerMember")
    public R registerMember(@RequestBody UserRegistVo userRegistVo){ //RequestBody表示请求体json数据转为对象
        try{
            memberService.registerMember(userRegistVo);
        }catch (PhoneNumberExistException  e){
            return R.error(15001,e.getMessage()); //返回异常信息
        }catch (UserNameExistException e){
            return R.error(15002,e.getMessage()); //返回异常信息
        }
        return R.ok();
    }

    /**
     *
     * 用户登录请求
     */
    @PostMapping("/login")
    public R memberLogin(@RequestBody UserLoginVo userLoginVo){
        //调用service进行登录验证
        MemberEntity memberEntity = memberService.verifyLogin(userLoginVo);
        if(memberEntity != null){
            return R.ok().put("data",memberEntity);
        }else{
            return R.error(BizCodeEnum.ACCOUNT_LOGIN_EXCEPTION.getCode(), BizCodeEnum.ACCOUNT_LOGIN_EXCEPTION.getMsg());
        }
    }

    //社交登录请求
    @PostMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {
        MemberEntity memberEntity =  memberService.login(socialUser);
        return R.ok().put("data",memberEntity);
    }

}
