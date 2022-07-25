package com.gulimall.gulimember.vo;

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
public class UserRegistVo {

    private  String userName;

    private String password;

    private String phoneNumber;

}
