package com.common.exception;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description: 运行时异常，检查手机号已存在
 **/
public class PhoneNumberExistException extends RuntimeException{
    public PhoneNumberExistException() {
        super("手机号已存在");
    }
}
