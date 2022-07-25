package com.gulimall.guliproduct.vo;

import lombok.Data;

/**
 * author:admin
 * date:2022/5/24
 * Info: 响应到前端的attr数据，带上所属属性分组和分类
 **/

@Data
public class AttrRespVo {
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Integer enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;

    //值类型（1.多选，0单选）
    private Integer valueType;

    //所属属性分组id
    private Long attrGroupId;

    //属性分组名
    private String groupName;

    //属性分类名
    private String catelogName;

    //所属分类id
    private Long[] catelogPath;
}
