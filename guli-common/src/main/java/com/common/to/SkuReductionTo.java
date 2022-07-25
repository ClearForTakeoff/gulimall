package com.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author:admin
 * date:2022/6/2
 * Info:
 **/

@Data
public class SkuReductionTo {
    private Long skuId;
    //满几件
    private int fullCount;
    //打几折
    private BigDecimal discount;
    //
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
