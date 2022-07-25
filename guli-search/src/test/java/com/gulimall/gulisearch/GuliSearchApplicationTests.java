package com.gulimall.gulisearch;

import com.alibaba.fastjson.JSON;
import com.gulimall.gulisearch.config.ElasticsearchConfig;
import io.swagger.models.auth.In;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
class GuliSearchApplicationTests {

    @Autowired
    RestHighLevelClient client;
    @Test
    void contextLoads() {
        System.out.println(client);
    }

    //测试查询数据
    @Test
    public void searchTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //设置查询的索引名
        searchRequest.indices("usertest");
        //添加查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("name","jerry"));

        searchRequest.source(searchSourceBuilder);
        //执行查询
        SearchResponse search = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        //结果分析
        System.out.println(search.toString());
    }
    //测试插入数据
    @Test
    public void indexTest() throws IOException {
        //
        IndexRequest usertest = new IndexRequest("usertest");
        usertest.id("1");//数据索引
        User user = new User();
        user.setAge(11);
        user.setName("jerry");
        String toJSONString = JSON.toJSONString(user);
        usertest.source(toJSONString, XContentType.JSON);//保存的内容
        IndexResponse index = client.index(usertest, ElasticsearchConfig.COMMON_OPTIONS);
        //
        System.out.println(index);
    }

    @Data
    static class User{
        private String name;
        private Integer age;
    }
}
