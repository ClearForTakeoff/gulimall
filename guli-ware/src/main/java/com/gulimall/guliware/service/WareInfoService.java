package com.gulimall.guliware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliware.entity.WareInfoEntity;
import com.gulimall.guliware.entity.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:50:27
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //带条件分页查询仓库
    PageUtils queryPageCondition(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

