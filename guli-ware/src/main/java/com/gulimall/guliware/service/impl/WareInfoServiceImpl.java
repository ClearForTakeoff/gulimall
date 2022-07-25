package com.gulimall.guliware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.gulimall.guliware.entity.vo.FareVo;
import com.gulimall.guliware.entity.vo.MemberAddressVo;
import com.gulimall.guliware.feign.MemberClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliware.dao.WareInfoDao;
import com.gulimall.guliware.entity.WareInfoEntity;
import com.gulimall.guliware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberClient memberClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        //得到查询条件
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wareInfoEntityQueryWrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);

        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();

        //收获地址的详细信息
        R addrInfo = memberClient.info(addrId);

        MemberAddressVo memberAddressVo = addrInfo.getDataByName("memberReceiveAddress",new TypeReference<MemberAddressVo>() {});

        if (memberAddressVo != null) {
            String phone = memberAddressVo.getPhone();

            BigDecimal bigDecimal = new BigDecimal(12);

            fareVo.setFare(bigDecimal);
            fareVo.setAddress(memberAddressVo);

            return fareVo;
        }
        return null;
    }
}