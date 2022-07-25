package com.gulimall.guliproduct.feign;

import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/15
 * @Description:
 **/
@FeignClient("guli-search")
public interface SearchFeignClient {

    //上架商品
    @RequestMapping("/search/save/product")
    public R upProduct(@RequestBody List<SkuEsModel> skuEsModels) ;
}
