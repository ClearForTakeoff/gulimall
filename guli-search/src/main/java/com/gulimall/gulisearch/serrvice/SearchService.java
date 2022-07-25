package com.gulimall.gulisearch.serrvice;

import com.gulimall.gulisearch.vo.SearchParam;
import com.gulimall.gulisearch.vo.SearchResult;

/**
 * @Author: duhang
 * @Date: 2022/6/19
 * @Description:
 **/
public interface SearchService {
    SearchResult searchProduct(SearchParam searchParam);
}
