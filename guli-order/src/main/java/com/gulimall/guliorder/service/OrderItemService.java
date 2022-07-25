package com.gulimall.guliorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliorder.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-06 00:03:45
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

