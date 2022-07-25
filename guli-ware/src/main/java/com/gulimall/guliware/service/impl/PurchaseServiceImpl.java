package com.gulimall.guliware.service.impl;

import com.common.constant.WareConstant;
import com.gulimall.guliware.entity.PurchaseDetailEntity;
import com.gulimall.guliware.service.PurchaseDetailService;
import com.gulimall.guliware.service.WareSkuService;
import com.gulimall.guliware.vo.MergeVo;
import com.gulimall.guliware.vo.PurchaseFinishVo;
import com.gulimall.guliware.vo.PurchaseItemFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliware.dao.PurchaseDao;
import com.gulimall.guliware.entity.PurchaseEntity;
import com.gulimall.guliware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    //查询未领取的采购单
    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)//0或者1表示采购单未被领取或者未完成
        );

        return new PageUtils(page);
    }

    //合并采购需求
    @Override
    @Transactional
    public void mergePurchase(MergeVo mergeVo) {
        //拿到采购单id
        Long purchaseId = mergeVo.getPurchaseId();
        //如果没有选择采购单，就不带有采购单id，此时需要新建采购单，并合并
        if(purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_CREATE.getCode());
            //新建采购单并保存
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //确定采购单的状态
        PurchaseEntity byId = this.getById(purchaseId);
        //采购单状态是0,1才可以被领取
        if(byId.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_CREATE.getCode() ||
        byId.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_ASSIGNED.getCode()){
            // 如果选择了采购单，传入的对象会带有采购单id，把采购需求合并进采购单
            //得到采购需求
            List<Long> items = mergeVo.getItems();
            //把采购单id，放到采购需求中
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map((entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity);
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                //状态设置为已分配
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_ASSIGNED.getCode());
                return purchaseDetailEntity;
            })).collect(Collectors.toList());
            //更新数据库
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        }



    }

    //领取采购单的方法
    @Override
    public void receive(List<Long> purchaseIds) {
        //确认采购单状态
        List<PurchaseEntity> collect = purchaseIds.stream().map((item -> {
            PurchaseEntity byId = this.getById(item);
            return byId;
        })).filter(item -> {
            //确定采购单状态是可以被领取的
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_CREATE.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            //修改采购单状态为被领取
            item.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_RECEIVED.getCode());
            return item;
        }).collect(Collectors.toList());
        //根据采购单的id,修改采购单的状态
        this.updateBatchById(collect);

        //修改采购需求的状态
        collect.forEach(item->{
            //查到采购单对应的采购需求
            List<PurchaseDetailEntity> entities = purchaseDetailService.getPurchaseDetailsByPurchaseId(item.getId());
            entities.forEach(entity->{
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_HANDLING.getCode());
            });

            //批量更新采购需求的状态
            purchaseDetailService.updateBatchById(entities);
        });
    }

    //完成采购单
    @Override
    public void finishPurchase(PurchaseFinishVo purchaseFinishVo) {
        //1.得到采购单
        Long purchaseId = purchaseFinishVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        //先设置为已完成,遍历采购需求如果有失败的再更新
        purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_FINISHED.getCode());
        //2.得到采购需求
        List<PurchaseItemFinishVo> items = purchaseFinishVo.getItems();
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            //得到采购需求id
            Long purchaseItemId = item.getPurchaseItemId();
            //采购需求状态
            Integer status = item.getStatus();
            //新建到采购需求
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(purchaseItemId);
            if (status == WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_HASERROR.getCode()) {
                purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_HASERROR.getCode());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_HASERROR.getCode());
            }else{
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_FINISHED.getCode());
                //将成功的采购进行入库操作
                // TODO: 2022/6/8
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getPurchaseItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        //更新所有的采购需求
        purchaseDetailService.updateBatchById(collect);
        //更新采购单
        this.updateById(purchaseEntity);
    }

}