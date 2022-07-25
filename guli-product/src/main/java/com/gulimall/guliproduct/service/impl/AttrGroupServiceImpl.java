package com.gulimall.guliproduct.service.impl;

import com.common.constant.ProductConstant;
import com.common.utils.Constant;
import com.gulimall.guliproduct.dao.AttrAttrgroupRelationDao;
import com.gulimall.guliproduct.dao.AttrDao;
import com.gulimall.guliproduct.entity.*;
import com.gulimall.guliproduct.service.AttrAttrgroupRelationService;
import com.gulimall.guliproduct.service.AttrService;
import com.gulimall.guliproduct.service.ProductAttrValueService;
import com.gulimall.guliproduct.vo.AttrAttrRelationVo;
import com.gulimall.guliproduct.vo.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.AttrGroupDao;
import com.gulimall.guliproduct.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    //注入关联表的Dao
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        //Key可以来查分组id和分组名
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        //传入的三级分类id为0，就查所有的
        if(categoryId == 0){
            return queryPage(params);
        }else{
            wrapper.eq("catelog_Id",categoryId);
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params), //分页条件
                    wrapper //查询条件
            );
            return new PageUtils(page);
        }
    }

    //查询到所有属性分组下的属性
    @Override
    public List<AttrEntity> getAttrGroupRelation(Long attrGroupId) {
        //1.根据属性分组id在关联表里面查找所有属性id
        QueryWrapper<AttrAttrgroupRelationEntity> relationEntityQueryWrapper = new QueryWrapper<>();
        relationEntityQueryWrapper.eq("attr_group_id",attrGroupId);
        relationEntityQueryWrapper.select("attr_id");
        List<AttrAttrgroupRelationEntity> attrEntities = attrAttrgroupRelationDao.selectList(relationEntityQueryWrapper);
        List<Long> attrIds = attrEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        //属性分组下没有属性时为空
        if(attrIds == null || attrIds.size() == 0){
            return null;
        }
        //2.根据属性id在attr表中查到所有属性
        //
        List<AttrEntity> attrEntities1 = attrService.listByIds(attrIds);
        return attrEntities1;
    }

    //批量删除
    @Override
    public void deleteRelation(AttrAttrRelationVo[] attrAttrRelationVos) {
        //数组转为流
        List<AttrAttrgroupRelationEntity> collect = Arrays.stream(attrAttrRelationVos).map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);

            return relationEntity;
        }).collect(Collectors.toList());
       attrAttrgroupRelationDao.deleteBatch(collect);
    }

    //根据分组id，查询到没有关联分组的属性
    @Override
    public PageUtils getNoAttrRelation(Map<String, Object> params, Long attrGroupId) {
        //1.查到分组所属的分类
        AttrGroupEntity attrGroupEntity = baseMapper.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.找到分组表里的所有分组
        QueryWrapper<AttrGroupEntity> groupWrapper = new QueryWrapper<>();
        groupWrapper.eq("catelog_id",catelogId);
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(groupWrapper);

        //拿到每个分组的id
        List<Long> groupIds = attrGroupEntities.stream().map((AttrGroupEntity::getAttrGroupId)).collect(Collectors.toList());

        //3.在关系表中找所有分组关联的属性
        QueryWrapper<AttrAttrgroupRelationEntity> relationEntityQueryWrapper = new QueryWrapper<>();
        relationEntityQueryWrapper.in("attr_group_id",groupIds);
        //找到了所有分组关联的属性
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(relationEntityQueryWrapper);
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        //4.到属性表中找不在上面查到属性列表里面的，同时attr_type=1的属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())
                .eq("catelog_id",catelogId);
        //查询到的ids可能是空的
        if(attrIds != null && attrIds.size() > 0){
            attrEntityQueryWrapper.notIn("attr_id",attrIds);
        }
        //模糊查询条件
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            attrEntityQueryWrapper.like("attr_name",key);
        }
        //查找
        IPage<AttrEntity> iPage = attrService.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );
        return new PageUtils(iPage);
    }

    //根据商品分类获取属性分组及其属性
    @Override
    public  List<AttrGroupAttrFrontVo> getAttrGroupByCatId(Long catId) {

        //1.查询到分组信息
        List<AttrGroupEntity> groupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        //2.根据分组查询分组的属性
        List<AttrGroupAttrFrontVo> collect = groupEntities.stream().map((item -> {
            AttrGroupAttrFrontVo attrGroupAttrFrontVo = new AttrGroupAttrFrontVo();
            BeanUtils.copyProperties(item, attrGroupAttrFrontVo);
            //查询分组包含的属性
            List<AttrEntity> attrGroupRelation = this.getAttrGroupRelation(item.getAttrGroupId());
            //赋值
            attrGroupAttrFrontVo.setAttrs(attrGroupRelation);
            return attrGroupAttrFrontVo;
        })).collect(Collectors.toList());
        return collect;
    }

    //查询spu对应的所有属性分组以及分组下属性的值
    @Override
    public List<SkuItemVo.SpuBaseAttrGroupVo> getSKUAttrGroupValueBySpuId(Long spuId,Long catalogId) {
        //根据分类id查询到spu的属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catalogId));

        //查出所有的属性分组关联
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationService.list();

        List<SkuItemVo.SpuBaseAttrGroupVo> res = attrGroupEntities.stream().map((attrGroup) -> {
            SkuItemVo.SpuBaseAttrGroupVo spuBaseAttrGroupVo = new SkuItemVo.SpuBaseAttrGroupVo();
            spuBaseAttrGroupVo.setAttrGroupName(attrGroup.getAttrGroupName());

            //根据属性分组id，拿到属性分组下的属性id
            Long attrGroupId = attrGroup.getAttrGroupId();
            ArrayList<Long> attrIds = new ArrayList<>();
            for (AttrAttrgroupRelationEntity attrAttrgroupRelationEntity : attrgroupRelationEntityList) {
                if (attrAttrgroupRelationEntity.getAttrGroupId() == attrGroupId) {
                    attrIds.add(attrAttrgroupRelationEntity.getAttrId());
                }
            }

            //根据属性分组的属性id，数据库查询属性值
            List<SkuItemVo.SpuBaseAttrVo> attrValue = attrIds.stream().map((attrId) -> {
                ProductAttrValueEntity one = productAttrValueService.getOne(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).eq("attr_id", attrId));
                SkuItemVo.SpuBaseAttrVo spuBaseAttrVo = new SkuItemVo.SpuBaseAttrVo();
                spuBaseAttrVo.setAttrName(one.getAttrName());
                spuBaseAttrVo.setAttrValue(one.getAttrValue());
                return spuBaseAttrVo;
            }).collect(Collectors.toList());


            spuBaseAttrGroupVo.setBaseAttrVos(attrValue);
            return spuBaseAttrGroupVo;
        }).collect(Collectors.toList());

        return res;
    }

}