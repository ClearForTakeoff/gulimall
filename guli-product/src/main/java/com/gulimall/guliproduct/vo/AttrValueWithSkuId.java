package com.gulimall.guliproduct.vo;

import lombok.Data;

/**
 * @Author: duhang
 * @Date: 2022/6/29
 * @Description: 带skuId的属性值
 **/
@Data
public class AttrValueWithSkuId {
    private String attrValue;
    private String skuIds;
}
