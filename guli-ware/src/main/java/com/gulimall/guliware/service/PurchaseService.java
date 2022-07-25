package com.gulimall.guliware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.gulimall.guliware.entity.PurchaseEntity;
import com.gulimall.guliware.vo.MergeVo;
import com.gulimall.guliware.vo.PurchaseFinishVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:50:27
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查询未领取的采购单
    PageUtils queryPageUnreceive(Map<String, Object> params);

    //合并采购需求
    void mergePurchase(MergeVo mergeVo);

    //领取采购单的方法
    void receive(List<Long> purchaseIds);

    //完成采购的方法
    void finishPurchase(PurchaseFinishVo purchaseFinishVo);
}

