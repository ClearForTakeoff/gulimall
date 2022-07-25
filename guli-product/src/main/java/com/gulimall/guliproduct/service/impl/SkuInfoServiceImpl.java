package com.gulimall.guliproduct.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.gulimall.guliproduct.entity.SkuImagesEntity;
import com.gulimall.guliproduct.entity.SpuInfoDescEntity;
import com.gulimall.guliproduct.feign.SecondKillClient;
import com.gulimall.guliproduct.service.*;
import com.gulimall.guliproduct.to.SeckillSkuTo;
import com.gulimall.guliproduct.vo.SkuItemAttrVo;
import com.gulimall.guliproduct.vo.SkuItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.SkuInfoDao;
import com.gulimall.guliproduct.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    //秒杀服务
    @Autowired
    SecondKillClient secondKillClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        //获取查询条件
        String key = (String) params.get("key");
        //为0表示查quanbu
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        QueryWrapper<SkuInfoEntity> skuInfoEntityQueryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            skuInfoEntityQueryWrapper.and(
                    (entity ->{
                        entity.eq("sku_id",key).or().like("sku_name",key);
                    })
            );
        }
        if(!StringUtils.isEmpty(brandId)){
            //不为0表示有值
            if(Long.parseLong(brandId) != 0L){
                skuInfoEntityQueryWrapper.eq("brand_id",brandId);
            }
        }
        if(!StringUtils.isEmpty(catelogId)){
            if(Long.parseLong(catelogId) != 0L){
                skuInfoEntityQueryWrapper.eq("catalog_id",catelogId);
            }
        }
        if(!StringUtils.isEmpty(min)){
            skuInfoEntityQueryWrapper.ge("price",min);
        }
        if(!StringUtils.isEmpty(max)){
            if(BigDecimal.valueOf(Long.parseLong(max)).compareTo(new BigDecimal(0)) > 0){
                skuInfoEntityQueryWrapper.le("price",max);
            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                skuInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    //根据spuId查询所有的sku
    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    //查询skuItem信息
    @Override
    public SkuItemVo getSkuItem(Long skuId) throws ExecutionException, InterruptedException {
        //构造对象设值
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //查询 : pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> spuDescFuture = infoFuture.thenAcceptAsync((res) -> {  //res异步任务的返回值
            //spu的描述信息
            SpuInfoDescEntity spuInfoDescServiceById = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpuInfoDescEntity(spuInfoDescServiceById);
        }, threadPoolExecutor);

        CompletableFuture<Void> skuSaleAttrFuture = infoFuture.thenAcceptAsync((res) -> {  //res异步任务的返回值
            //sku的销售属性
            List<SkuItemAttrVo> attrVos = skuSaleAttrValueService.getSaleAttrGroup(res.getSpuId());
            skuItemVo.setAttrVos(attrVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> spuBaseAttrFuture = infoFuture.thenAcceptAsync((res) -> {  //res异步任务的返回值
            //spu的基本属性
            List<SkuItemVo.SpuBaseAttrGroupVo> spuItemBaseAttrVos = attrGroupService.getSKUAttrGroupValueBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setSpuItemBaseAttrVos(spuItemBaseAttrVos);
        }, threadPoolExecutor);


        CompletableFuture<Void> skuImageFuture = CompletableFuture.runAsync(() -> {
            //查询sku图片 pms_sku_images
            List<SkuImagesEntity> imagesList = skuImagesService.listBySkuId(skuId);
            skuItemVo.setSkuImagesEntities(imagesList);
        }, threadPoolExecutor);

        CompletableFuture<Void> seckill = CompletableFuture.runAsync(() -> {
            //异步任务查询商品是否参与秒杀
            R skuSecKill = secondKillClient.getSkuSecKill(skuId);
            if (skuSecKill.getCode() == 0) {
                SeckillSkuTo data = skuSecKill.getData(new TypeReference<SeckillSkuTo>() {
                });
                skuItemVo.setSeckillSkuTo(data);
            }
        }, threadPoolExecutor);

        //等待所有异步任务完成后返回
        CompletableFuture.allOf( spuDescFuture, skuSaleAttrFuture, spuBaseAttrFuture, skuImageFuture,seckill).get();
        return skuItemVo;

    }

    //查询sku价格
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return baseMapper.selectSkuPrice(skuId);
    }
}