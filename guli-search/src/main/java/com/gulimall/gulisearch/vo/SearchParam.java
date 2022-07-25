package com.gulimall.gulisearch.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/19
 * @Description: 用于封装检索条件对象
 **/
@Data
public class SearchParam{
    //查询条件

    //全文检索
    private String keyword;
    //分类id
    private Long catalog3Id;

    //排序条件
    //销量，价格，热度排序
    private String sort;

    //查询条件
    private Integer hasStock;   //是否有货
    //
    private String skuPrice ;//价格区间查询

    //品牌id查询
    private List<Long> brandIds;
    //属性筛选
    private List<String> attrs;

    //分页查询，页码
    private Integer pageNum = 1;

    private String searchUrl;//浏览器链接带的参数
}
