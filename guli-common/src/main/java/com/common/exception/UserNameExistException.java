package com.common.exception;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description:
 **/
public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
        super("用户名已存在");
    }
}
