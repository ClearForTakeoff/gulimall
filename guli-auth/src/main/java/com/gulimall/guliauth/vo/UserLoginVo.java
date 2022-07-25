package com.gulimall.guliauth.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author: duhang
 * @Date: 2022/7/1
 * @Description:
 **/
@Data
public class UserLoginVo {
    private String account;
    private String password;
}
