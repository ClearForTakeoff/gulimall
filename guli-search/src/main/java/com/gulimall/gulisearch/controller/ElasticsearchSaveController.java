package com.gulimall.gulisearch.controller;

import com.common.exception.BizCodeEnum;
import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import com.gulimall.gulisearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/14
 * @Description: es保存数据的controller
 **/

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticsearchSaveController {

    @Autowired
    ProductSaveService productSaveService;

    //上架商品
    @RequestMapping("/product")
    public R upProduct(@RequestBody List<SkuEsModel> skuEsModels) {
        try {
            productSaveService.saveProduct(skuEsModels);
        } catch (IOException e) {
            log.error("商品上架出错");
            return R.error(88000,"商品上架异常" + e);
        }
        return R.ok();
    }

}
