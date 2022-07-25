package com.gulimall.gulisearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.constant.ProductConstant;
import com.common.to.es.SkuEsModel;
import com.gulimall.gulisearch.config.ElasticsearchConfig;
import com.gulimall.gulisearch.constant.EsConstant;
import com.gulimall.gulisearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/14
 * @Description:
 **/
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    //输入client
    @Autowired
    RestHighLevelClient restHighLevelClient;

    //上架商品到elastic
    @Override
    public void saveProduct(List<SkuEsModel> skuEsModels) throws IOException {
        //商品信息在elastic中的索引是 product
        //批量保存
        //1
        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsModel skuEsModel : skuEsModels) {
            //保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.ES_INDEX);
            //设置数据id
            indexRequest.id(String.valueOf(skuEsModel.getSkuId()));
            //数据转换为json字符串
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        //保存失败
        boolean failures = bulk.hasFailures();
        if(failures){
            log.error("商品上架异常");
        }else {
            log.info("商品上架成功");
        }
    }
}
