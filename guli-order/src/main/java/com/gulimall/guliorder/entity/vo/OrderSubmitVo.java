package com.gulimall.guliorder.entity.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author: duhang
 * @Date: 2022/7/8
 * @Description:
 **/
@Data
@ToString
public class OrderSubmitVo {
    private Long addrId;//地址id
    private Integer payType;//支付类型
    private String orderToken;//令牌
    private BigDecimal payPrice;//支付金额


}
