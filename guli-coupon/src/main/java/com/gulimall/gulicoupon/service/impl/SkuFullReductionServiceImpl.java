package com.gulimall.gulicoupon.service.impl;

import com.common.to.MemberPrice;
import com.common.to.SkuReductionTo;
import com.gulimall.gulicoupon.entity.MemberPriceEntity;
import com.gulimall.gulicoupon.entity.SkuLadderEntity;
import com.gulimall.gulicoupon.service.MemberPriceService;
import com.gulimall.gulicoupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.gulicoupon.dao.SkuFullReductionDao;
import com.gulimall.gulicoupon.entity.SkuFullReductionEntity;
import com.gulimall.gulicoupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    //保存满级按信息
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1.保存满几件打折 sms_sku_ladder
        //满0件，打折都是无效
        if(skuReductionTo.getFullCount() > 0){
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }
        //有设置才保存,满减金额都应该大于0 ，满0元减0元，无效
        if(skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0 && skuReductionTo.getReducePrice().compareTo(new BigDecimal(0)) > 0){
            //2.保存满一定金额减几元 sms_sku_full_reduction
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(1);
            this.save(skuFullReductionEntity);
        }

        //3.设置会员价格 sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map((item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        })).filter(entity ->{
            return entity.getMemberPrice().compareTo(new BigDecimal("0")) > 0;
        }).collect(Collectors.toList());//过滤会员价格为0的情况
        memberPriceService.saveBatch(collect);
    }

}