package com.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: duhang
 * @Date: 2022/7/16
 * @Description: 秒杀订单的to
 **/
@Data
public class SeckillOrderTo {
    private String orderSn; //订单号

    //秒杀的商品信息
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 购买数量
     */
    private Integer num;

    private Long memberId;//会员id
}
