package com.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author:admin
 * date:2022/6/2
 * Info: 用于远程调用是封装数据的对象,积分信息
 *  远程调用请求数据的类型，中的属性名要与被调用的方法中接收的类型中属性一致
 **/

@Data
public class SpuBonusTo {
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

}
