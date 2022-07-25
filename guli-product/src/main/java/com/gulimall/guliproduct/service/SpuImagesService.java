package com.gulimall.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliproduct.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //保存spu图片
    void saveImages(Long id, List<String> images);
}

