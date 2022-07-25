package com.common.exception;


/**
 * author:admin
 * date:2022/5/19
 * Info: 自定义统一异常
 **/


public enum BizCodeEnum {
    UNKNOWN_EXCEPTION (10001,"未知异常"),
    VALID_EXCEPTION (11002,"参数校验失败"),
    MESSAGE_SEND_EXCEPTION(15004,"验证码发送失败"),
    ACCOUNT_LOGIN_EXCEPTION(15005,"账户或密码错误"),
    NO_STOCK_EXCEPTION(12001,"商品没有库存"),
    TOO_MANY_REQUEST(13001,"太多请求")
    ;
    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    BizCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
