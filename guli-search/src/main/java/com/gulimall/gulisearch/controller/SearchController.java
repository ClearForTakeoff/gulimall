package com.gulimall.gulisearch.controller;

import com.gulimall.gulisearch.serrvice.SearchService;
import com.gulimall.gulisearch.vo.SearchParam;
import com.gulimall.gulisearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: duhang
 * @Date: 2022/6/19
 * @Description:
 **/
@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/list.html")
    public String list(SearchParam searchParam, Model model, HttpServletRequest request){
        String queryString = request.getQueryString();
        searchParam.setSearchUrl(queryString);
        //根据查询条件去es中检索商品，返回相关信息
        SearchResult searchResult = searchService.searchProduct(searchParam);
        model.addAttribute("searchResult",searchResult);
        return "list";
    }
}
