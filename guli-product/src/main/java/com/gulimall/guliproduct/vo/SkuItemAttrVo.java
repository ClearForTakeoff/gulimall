package com.gulimall.guliproduct.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/28
 * @Description:
 **/
    @Data
    public  class SkuItemAttrVo{
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuId> attrValues;
    }
