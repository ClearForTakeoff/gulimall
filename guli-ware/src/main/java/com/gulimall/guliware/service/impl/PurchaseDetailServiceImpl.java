package com.gulimall.guliware.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliware.dao.PurchaseDetailDao;
import com.gulimall.guliware.entity.PurchaseDetailEntity;
import com.gulimall.guliware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();

        //
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(key)){
            purchaseDetailEntityQueryWrapper.and((entity ->{
                entity.eq("purchase_id",key)
                        .or().eq("sku_id",key)
                        .or().eq("sku_num",key)
                        .or().eq("sku_price",key);
            }));
        }
        if(!StringUtils.isEmpty(status)){
            purchaseDetailEntityQueryWrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(wareId)){
            purchaseDetailEntityQueryWrapper.eq("ware_id",wareId);

        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                purchaseDetailEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    //根据采购单id得到采购需求
    @Override
    public List<PurchaseDetailEntity> getPurchaseDetailsByPurchaseId(Long id) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();
        purchaseDetailEntityQueryWrapper.eq("purchase_id",id);
        List<PurchaseDetailEntity> list = this.list(purchaseDetailEntityQueryWrapper);
        return list;
    }

}