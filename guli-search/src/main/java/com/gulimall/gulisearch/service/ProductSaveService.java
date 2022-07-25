package com.gulimall.gulisearch.service;

import com.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/14
 * @Description:
 **/

public interface ProductSaveService {
    //上架商品信息到elastic
    void saveProduct(List<SkuEsModel> skuEsModels) throws IOException;
}
