package com.gulimall.gulisearch.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.to.es.SkuEsModel;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.elasticsearch.search.aggregations.metrics.InternalGeoBounds;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/19
 * @Description: 封装返回的数据信息
 **/
@Data
public class SearchResult {

    //查询的数据
    private List<SkuEsModel> products;
    //页码
    private Integer pageNum;
    //总记录数
    private Long total;
    //总页数
    private Integer totalPages;
    //导航页码
    private List<Integer> pageNav;

    //封装涉及的 品牌信息
    private List<BrandVo> brandvos;

    @Data
    public static class BrandVo{
        //@JSONField(serializeUsing = ToStringSerializer.class)
        private String brandId;
        private String brandName;
        private String brandImg;
    }

    private List<AttrVo> attrVos;   //查询结构涉及的属性
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }

    private List<CategoryVo> categoryVos; //当前查询结果涉及的分类

    @Data
    public static class CategoryVo{
        private Long catalogId;
        private String catalogName;
    }

    //面包屑导航数据
    private List<NavVo> navs;
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }
}

