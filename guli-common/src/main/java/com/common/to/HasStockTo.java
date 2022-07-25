package com.common.to;

import lombok.Data;

/**
 * @Author: duhang
 * @Date: 2022/6/14
 * @Description: 封装每个sku的是否有库存
 **/
@Data
public class HasStockTo {
    private Long skuId;
    private Boolean hasStock;
}
