package com.gulimall.gulisearch.serrvice.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.to.AttrRespTo;
import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import com.gulimall.gulisearch.client.ProductFeignClient;
import com.gulimall.gulisearch.config.ElasticsearchConfig;
import com.gulimall.gulisearch.constant.EsConstant;
import com.gulimall.gulisearch.serrvice.SearchService;
import com.gulimall.gulisearch.vo.SearchParam;
import com.gulimall.gulisearch.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: duhang
 * @Date: 2022/6/19
 * @Description:
 **/
@Service
public class SearchServiceImpl implements SearchService {

    //es中检索商品
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    ProductFeignClient feignClient;

    @Override
    public SearchResult searchProduct(SearchParam searchParam) {
        //检索请求
        SearchRequest searchRequest = null;
        SearchResult searchResult = null;
        //整合检索请求
        searchRequest = buildSearchRequest(searchParam);
        //2.执行检索
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
            //分装结果
            searchResult = buildSearchResult(searchResponse,searchParam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return searchResult;
    }

    private SearchResult buildSearchResult(SearchResponse searchResponse,SearchParam searchParam) {
        SearchResult result = new SearchResult();
        //拿到命中的记录
        SearchHits hits = searchResponse.getHits();
//查询的数据
//        private List<SkuEsModel> products;
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        if (hits != null && hits.getHits().length > 0){
            //遍历hits
            for (SearchHit hit : hits.getHits()) {
                //获取字符串形式的数据
                String sourceAsString = hit.getSourceAsString();
                //转为skuModel
                SkuEsModel skuModel = JSON.parseObject(sourceAsString, new TypeReference<SkuEsModel>() {});

                //获取高亮信息
                if(!StringUtils.isEmpty(searchParam.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuModel.setSkuTitle(string);
                }

                skuEsModels.add(skuModel);
            }
        }
        result.setProducts(skuEsModels);
        //页码
            //        private Integer pageNum;
            result.setPageNum(searchParam.getPageNum());
        //总记录数
//        private Integer total;
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //总页数
//        private Integer totalPages;
        result.setTotalPages((int) (total % EsConstant.PAGE_SEIZE == 0 ? total / EsConstant.PAGE_SEIZE : total / EsConstant.PAGE_SEIZE + 1));
        ArrayList<Integer> pageNav = new ArrayList<>();
        //设置导航页码
        for (Integer i = 1; i <= result.getTotalPages(); i++) {
            pageNav.add(i);
        }
        result.setPageNav(pageNav);
        //-----------聚合信息==================================
        //封装涉及的 品牌信息
//        private List<SearchResult.BrandVo> brandvos;
        //获取集合信息
        Aggregations aggregations = searchResponse.getAggregations();

        ParsedLongTerms category_agg = aggregations.get("category_agg");
        List<SearchResult.CategoryVo> categoryVos = new ArrayList<>();
        for (Terms.Bucket bucket : category_agg.getBuckets()) {
            //创建要封装的对象
            SearchResult.CategoryVo vo = new SearchResult.CategoryVo();

            //拿到分类id
            Long key = (Long) bucket.getKey();
            vo.setCatalogId(key);
            //拿到子聚合,分类名
            ParsedStringTerms aggregations1 = bucket.getAggregations().get("category_name_agg");
            String catalogName = aggregations1.getBuckets().get(0).getKeyAsString();
            vo.setCatalogName(catalogName);
            categoryVos.add(vo);
        }
        result.setCategoryVos(categoryVos);

//        private List<SearchResult.AttrVo> attrVos;   //查询结构涉及的属性
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //拿到attr_id
            String attr_id = bucket.getKeyAsString();
            attrVo.setAttrId(Long.valueOf(attr_id));
            //获取attr_name
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attr_name = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            //获取attr_value
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            // todo attrValue是list
            List<String> attr_value_list = new ArrayList<>();
            for (Terms.Bucket attr_name_aggBucket : attr_value_agg.getBuckets()) {
                String attr_value = attr_name_aggBucket.getKeyAsString();
                attr_value_list.add(attr_value);
            }
            attrVo.setAttrValues(attr_value_list);
            attrVos.add(attrVo);
        }
        result.setAttrVos(attrVos);
//        private List<SearchResult.CategoryVo> categoryVos; //当前查询结果涉及的分类
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //拿到品牌id
            String keyAsString = bucket.getKeyAsString();
            brandVo.setBrandId(keyAsString);
            //拿到品牌名和品牌图片
            //拿到子聚合
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);

            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);
            brandVos.add(brandVo);
        }
        result.setBrandvos(brandVos);

        //面包屑导航数据
        if(searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0){
            List<SearchResult.NavVo> collect = searchParam.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //attr前端穿的字符串数据  attrs=attrId_attrValue:attrValue
                String[] split = attr.split("_");
                //远程调用差attrName todo
                R info = feignClient.info(Long.valueOf(split[0]));
                if((Integer)info.get("code") == 0){
                    AttrRespTo byName = info.getDataByName("attr", new TypeReference<AttrRespTo>() {
                    });
                    navVo.setNavName(byName.getAttrName());
                    navVo.setNavValue(split[1]);
                }else{
                    navVo.setNavName(split[0]);
                }
                //取消面包屑导航之后，要将请求地址url的参数替换
                String searchUrl = searchParam.getSearchUrl();
                //拿到请求参数，进行替换
                //对中文进行编码
                String encode = null;
                try {
                     encode = URLEncoder.encode(attr, "UTF-8");
                     encode.replace("+","%20");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                searchUrl.replace("&attrs="+encode,"");
                navVo.setLink("http://search.gulimall.com/list.htnl?");
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }

        return result;
    }

    //构建请求
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //sourceBuilder构建检索请求
        //1.查询；
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // must条件
        // （1）.全文检索关键词
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }
        //（2）.过滤查询:
        //三级分类id
        if(searchParam.getCatalog3Id() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        //品牌id
        if(searchParam.getBrandIds() != null && searchParam.getBrandIds().size() > 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandIds()));
        }
        //指定的属性查询
        if(searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0){
            //参数格式 属性id_属性值1：属性值2
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder nestQueryBuilder = QueryBuilders.boolQuery();//每一个条件生成一个builder
                String[] s = attr.split("_");
                String attrId = s[0]; //属性id
                String[] attrValue = s[1].split(":"); //属性值
                nestQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs", nestQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(attrs);
            }
        }
        //是否有库存
        if(searchParam.getHasStock() != null){

            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }

        //价格区间检索
        if(!StringUtils.isEmpty(searchParam.getSkuPrice())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            //分析skuPrice
            //skuPrice 有三种形式 _price , price_price,price_
            String skuPrice = searchParam.getSkuPrice();
            String[] s = skuPrice.split("_");
            //s长度为2就是区间
            if(s.length == 2){
                rangeQueryBuilder.gte(s[0]).lte(s[1]);

            }
            //s长度为1就是大于或者小于
            else if(s.length == 1){
                //小于的情况
                if(skuPrice.startsWith("_")){
                    rangeQueryBuilder.lte(s[0]);
                }else{ //大于的情况
                    rangeQueryBuilder.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        //封装到sourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);

        //2.排序.参数格式 属性名_升序/降序
        if(!StringUtils.isEmpty(searchParam.getSort())){
            String sort = searchParam.getSort();
            String[] splite = sort.split("_");
            String sortName = splite[0];
            String order = splite[1];
            if(order.equalsIgnoreCase("asc")){
                searchSourceBuilder.sort(sortName, SortOrder.ASC);
            }else{
                searchSourceBuilder.sort(sortName, SortOrder.DESC);
            }
        }

        //3.分页
        // pageNum = 1 from 0 PageSize:2        0,1,2
        searchSourceBuilder.from( (searchParam.getPageNum() - 1) * EsConstant.PAGE_SEIZE);
        searchSourceBuilder.size(EsConstant.PAGE_SEIZE);


        //3.高亮
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            //前置后置标签
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        //4.聚合分析
        //(1)品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(10);
            //子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);
        //(2)。分类聚合
        TermsAggregationBuilder category_agg = AggregationBuilders.terms("category_agg").field("catalogId").size(10);
        category_agg.subAggregation(AggregationBuilders.terms("category_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(category_agg);

        //(3).属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        //子聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //子聚合的子聚合
        //分析出attrId对应的属性名字和属性值
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10));

        //放入上一级聚合
        nested.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(nested);

        System.out.println(searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[] {EsConstant.ES_INDEX},searchSourceBuilder);
        return searchRequest;
    }
}
