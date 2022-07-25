package com.gulimall.gulicart.vo;

import lombok.Data;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description:
 **/
@Data
public class UserInfo {
    private String userKey;
    private Long userId;
    private boolean isTempUser = true; //true表示要创建cookie
}
