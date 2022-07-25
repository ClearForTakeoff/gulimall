package com.gulimall.gulimember.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.PhoneNumberExistException;
import com.common.exception.UserNameExistException;
import com.common.to.SocialUser;
import com.common.to.UserLoginVo;
import com.common.utils.HttpUtils;
import com.gulimall.gulimember.entity.MemberLevelEntity;
import com.gulimall.gulimember.service.MemberLevelService;
import com.gulimall.gulimember.vo.UserRegistVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.gulimember.dao.MemberDao;
import com.gulimall.gulimember.entity.MemberEntity;
import com.gulimall.gulimember.service.MemberService;
import org.springframework.util.NumberUtils;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    //注册会员
    @Override
    public void registerMember(UserRegistVo userRegistVo) {
        //检查用户名和手机号的唯一性
        checkUserName(userRegistVo.getUserName());
        checkPhoneNUmber(userRegistVo.getPhoneNumber());
        //校验不通过直接抛异常
        //校验通过，构建对象，设值
        MemberEntity memberEntity = new MemberEntity();
        //设值默认的会员等级
        //查询出默认等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        //设值前端的注册信息
        memberEntity.setUsername(userRegistVo.getUserName());
        memberEntity.setNickname(userRegistVo.getUserName());
        memberEntity.setMobile(userRegistVo.getPhoneNumber());
        //密码进行加密存储
        //MD5加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //编码后的密码
        String encode = bCryptPasswordEncoder.encode(userRegistVo.getPassword());
        memberEntity.setPassword(encode);
        baseMapper.insert(memberEntity);

    }



    //检查用户名和手机号的唯一性
    private void checkPhoneNUmber(String phoneNumber) throws PhoneNumberExistException {
        int count = baseMapper.countPhoneNumber(phoneNumber);
        if(count > 0){
            throw new PhoneNumberExistException();
        }
    }

    private void checkUserName(String userName) throws UserNameExistException{
        int count = baseMapper.countUsername(userName);
        if(count > 0){
            throw new UserNameExistException();
        }
    }


    //验证用户登录
    @Override
    public MemberEntity  verifyLogin(UserLoginVo userLoginVo) {
        String account = userLoginVo.getAccount();
        String password = userLoginVo.getPassword();
        //数据库查询用户名
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", account).or().eq("mobile", account));
        //查不到，返回异常：用户不存在
        if(memberEntity == null){
            return null;
        }
        //查到了，验证密码
        String passwordDb = memberEntity.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(password, passwordDb);
        if(matches){
            //密码正确，返回success
            return memberEntity;
        }else{
            //密码错误，返回异常：密码错误
           return null;
        }
    }

    //用户社交登录
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        //1.查询数据库 social_uid
        String uid = socialUser.getUid();
        //查询有没有uid
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity != null){ //有的话表示登陆过
            //更新令牌信息
            MemberEntity updateEntity = new MemberEntity();
            BeanUtils.copyProperties(memberEntity,updateEntity);
            updateEntity.setAccessToken(socialUser.getAccess_token());
            updateEntity.setExpiresIn(socialUser.getExpires_in());
            //更新用户信息
            baseMapper.updateById(updateEntity);
            //返回
            return updateEntity;
        }else{
            //没有的话表示没有注册过，需要进行注册
            MemberEntity register = new MemberEntity();
            try {
                //封装查询的信息
                HashMap<String, String> queryMap = new HashMap<>();
                queryMap.put("access_token",socialUser.getAccess_token());
                queryMap.put("uid",socialUser.getUid());
                //https://api.weibo.com/2/users/show.json

                HttpResponse httpResponse = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get",
                        new HashMap<String, String>(), queryMap);
                if(httpResponse.getStatusLine().getStatusCode() == 200){//请求成功
                    //查询成功
                    String json = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    String profileImageUrl = jsonObject.getString("profile_image_url");

                    register.setNickname(name);
                    register.setGender("m".equals(gender)?1:0);
                    register.setHeader(profileImageUrl);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //如果远程查询失败了，只封装这三条信息
            register.setSocialUid(socialUser.getUid());
            register.setAccessToken(socialUser.getAccess_token());
            register.setExpiresIn(socialUser.getExpires_in());

            //把用户信息插入到数据库中
            this.baseMapper.insert(register);
            return register;
        }
    }
}