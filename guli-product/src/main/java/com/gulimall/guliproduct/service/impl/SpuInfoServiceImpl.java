package com.gulimall.guliproduct.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.common.constant.ProductConstant;
import com.common.to.HasStockTo;
import com.common.to.SkuReductionTo;
import com.common.to.SpuBonusTo;
import com.common.to.SpuInfoTo;
import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import com.gulimall.guliproduct.entity.*;
import com.gulimall.guliproduct.feign.CouponFeignClient;
import com.gulimall.guliproduct.feign.SearchFeignClient;
import com.gulimall.guliproduct.feign.WareFeignClient;
import com.gulimall.guliproduct.service.*;
import com.gulimall.guliproduct.vo.*;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import sun.util.resources.cldr.nmg.LocaleNames_nmg;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    AttrService attrService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignClient  couponFeignClient;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    //注入库存服务远程客户端
    @Autowired
    WareFeignClient wareFeignClient;

    //注入search远程服务
    @Autowired
    SearchFeignClient searchFeignClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    //保存商品
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {

        //1.保存基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        //前端传的catalogId后端是catelogId
        spuInfoEntity.setCatelogId(spuInfo.getCatalogId());
        this.saveSpuInfoEntity(spuInfoEntity);
        //2.保存spu描述信息 pms_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuDescrip(spuInfoDescEntity);
        //3.保存spu图片 pms_spu_images
        List<String> images = spuInfo.getImages();
        imagesService.saveImages(spuInfoEntity.getId(),images);
        //4.保存spu属性参数信息 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> attrValues = baseAttrs.stream().map((attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrId(attr.getAttrId());
            //根据attrId查到attrName
            AttrEntity byId = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(byId.getAttrName());

            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            return productAttrValueEntity;
        })).collect(Collectors.toList());
        productAttrValueService.saveBatch(attrValues);
        //5.保存spu积分信息,金币和成长值
        Bounds bounds = spuInfo.getBounds();
        SpuBonusTo spuBonusTo = new SpuBonusTo();
        BeanUtils.copyProperties(bounds,spuBonusTo);
        spuBonusTo.setSpuId(spuInfoEntity.getId());
        //调用远程服务
        R save = couponFeignClient.save(spuBonusTo);
        if((Integer)save.get("code") == 1){
            log.error("远程调用失败：spu商品积分信息失败");
        }

        //6.保存sku
        List<Skus> spuInfoSkus = spuInfo.getSkus();

        if(spuInfoSkus != null && spuInfoSkus.size() > 0){
            //遍历每一个sku
            for (Skus item : spuInfoSkus) {
                //(1)sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //仅复制了表中的四个属性
                //private String skuName;
                //    private BigDecimal price;
                //    private String skuTitle;
                //    private String skuSubtitle;
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDesc(String.join(",", item.getDescar()));
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatelogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                //获取sku的图片
                List<Images> skuImages = item.getImages();
                skuImages.forEach((img) -> {
                    if (img.getDefaultImg() == 1) {
                        skuInfoEntity.setSkuDefaultImg(img.getImgUrl());
                    }
                });
                skuInfoService.saveSkuInfo(skuInfoEntity);
                //得到自增的id
                Long skuId = skuInfoEntity.getSkuId();

                //(2)sku描述图片 pms_sku_images
                //得到sku的图片
                List<SkuImagesEntity> skuImagesEntityList = skuImages.stream().map((img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;
                })).filter(skuImagesEntity ->{return !StringUtils.isEmpty(skuImagesEntity.getImgUrl()); }).collect(Collectors.toList()); //使用filter过滤掉空的图片，
                skuImagesService.saveBatch(skuImagesEntityList);

                //(3)sku销售属性信息  pms_sku_sale_attr_value
                List<Attr> skuAttr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuAttr.stream().map((attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    BeanUtils.copyProperties(attr,skuSaleAttrValueEntity);
                    return skuSaleAttrValueEntity;
                })).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //(4)sku优惠满减信息
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //过滤满减信息，满件数为0 ，满金额为0，过滤掉
                if(skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0){
                    R r = couponFeignClient.saveSkuReduction(skuReductionTo);
                    if((Integer)r.get("code") == 1){
                        log.error("远程调用失败：spu商品积分信息失败");
                    }
                }
            }
        }




    }

    @Override
    public void saveSpuInfoEntity(SpuInfoEntity spuInfo) {
        baseMapper.insert(spuInfo);
    }

    //条件查询所有的spu
    @Override
    public PageUtils queryPageContidition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        //获取检索条件
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        if(!StringUtils.isEmpty(key)){
            //key作为查询条件
            spuInfoEntityQueryWrapper.and(
                    (wrapper) -> {
                        wrapper.eq("id", key).or().like("spu_name", key);
                    });
        }
        //spu状态
        if(!StringUtils.isEmpty(status)){
            spuInfoEntityQueryWrapper.eq("publish_status",status);
        }
        if(!StringUtils.isEmpty(brandId)){
            if( Long.parseLong(brandId) != 0L){
                spuInfoEntityQueryWrapper.eq("brand_id",brandId);
            }
        }
        if(!StringUtils.isEmpty(catelogId)){
            if(Long.parseLong(catelogId) != 0L){
                spuInfoEntityQueryWrapper.eq("catelog_id",catelogId);
            }
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    //上架商品
    @Override
    public void up(Long spuId) {

        //1.查询spu对应的sku
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuBySpuId(spuId);

        //设置每个sku的可以被检索的基本属性，根据spuId进行查询，没有sku的这一属性都一样
        //(6)查询到spu所有属性
        //根据spuId查到所有的基本属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.listSpuAttrValue(spuId);
        //（7）拿到属性的id
        List<Long> attrIds = productAttrValueEntities.stream().map((ProductAttrValueEntity::getAttrId)).collect(Collectors.toList());

        //（8）查到基本属性的id之后到attr表中查search_type==1的属性id
        List<Long> attrIdsSearch = attrService.getSearchAttrIds(attrIds);
        //转换为set进行筛选
        HashSet<Long> idsSet = new HashSet<>(attrIdsSearch);

        //（9)在productAttrValueEntities中进行过滤,再封装的属性对象数组
        List<SkuEsModel.Attrs> collect = productAttrValueEntities.stream().filter(item -> {
            return idsSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item,attrs);
            return attrs;
        }).collect(Collectors.toList());

        //2调用远程服务查询库存
        //拿到skuids
        List<Long> stockSkuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        //如果远程调用出现异常
        Map<Long, Boolean> skuHasStockMap = null;
        try {
            //要将返回的R对象中的object类型对象，转为需要的复杂类型对象
            List<HasStockTo> hasStockTos = wareFeignClient.hasStock(stockSkuIds).getData(new TypeReference<List<HasStockTo>>(){});
            //把hasStockTos封装为map
            skuHasStockMap = hasStockTos.stream().collect(Collectors.toMap(HasStockTo::getSkuId, HasStockTo::getHasStock));
        } catch (Exception e) {
            log.error("远程调用 wareFeignClient 出现异常 :" + e.getMessage());
        }

        //3.封装数据
        Map<Long, Boolean> finalSkuHasStockMap = skuHasStockMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map((entity -> {
            //(1)创建es数据模型对象
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(entity, skuEsModel);

            //(2)设置其他信息
            skuEsModel.setSkuPrice(entity.getPrice());
            skuEsModel.setSkuImg(entity.getSkuDefaultImg());

            //（3）设置是否包含库存 hasStock
            //如果远程调用异常 map为空 ,默认有库存
            if(finalSkuHasStockMap == null){
                skuEsModel.setHasStock(true);
            }else{
                skuEsModel.setHasStock(finalSkuHasStockMap.get(skuEsModel.getSkuId()));
            }

            //todo
            //     Long hotScore;
            //（4)封装以下属性
            //     String brandName;
            //     String brandImg;
            BrandEntity brandEntityById = brandService.getById(entity.getBrandId());
            skuEsModel.setBrandName(brandEntityById.getName());
            skuEsModel.setBrandImg(brandEntityById.getLogo());
            //(5)封装属性
            //     String catalogName;
            CategoryEntity categoryEntityById = categoryService.getById(entity.getCatalogId());
            skuEsModel.setCatalogName(categoryEntityById.getName());

            //     List<Attrs> attrs 赋值
            //             Long attrId;
            //             String attrName;
            //             String attrValue;
            skuEsModel.setAttrs(collect);

            return skuEsModel;
        })).collect(Collectors.toList());


        //todo
        //把数据发送给guli-search保存
        R r = searchFeignClient.upProduct(skuEsModels);
        if((int) r.get("code") == 0){
            //远程调用成功
            //修改spu状态
            baseMapper.updatePublishStatus(spuId, ProductConstant.SpuPublishStatus.SPU_UP.getStatus());
        }else{
            // todo 失败

        }
    }

    //根据skuId获取spu信息
    @Override
    public SpuInfoTo getSpuInfoBySkuId(Long skuId) {
        //获取spu的id
        SkuInfoEntity byId = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfoEntity = baseMapper.selectById(byId.getSpuId());
        SpuInfoTo spuInfoTo = new SpuInfoTo();
        BeanUtils.copyProperties(spuInfoEntity,spuInfoTo);
        //查询品牌名
        BrandEntity brand = brandService.getById(spuInfoEntity.getBrandId());
        spuInfoTo.setBrandName(brand.getName());
        return spuInfoTo;
    }
}