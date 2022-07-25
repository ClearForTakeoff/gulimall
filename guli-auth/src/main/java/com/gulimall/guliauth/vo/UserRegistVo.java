package com.gulimall.guliauth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description: 用户注册封装数据
 **/
@Data
@AllArgsConstructor
public class UserRegistVo {
    @NotNull(message = "用户名不能为空")
    @Length(min = 6,max = 12,message = "用户名必须是6-12位")
    private  String userName;
    @NotNull(message = "密码不能为空")
    @Length(min = 6,max = 12,message = "密码必须是6-12位")
    private String password;
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phoneNumber;
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
