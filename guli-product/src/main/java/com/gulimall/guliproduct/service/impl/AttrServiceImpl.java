package com.gulimall.guliproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.common.constant.ProductConstant;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.gulimall.guliproduct.dao.AttrAttrgroupRelationDao;
import com.gulimall.guliproduct.dao.AttrGroupDao;
import com.gulimall.guliproduct.dao.CategoryDao;
import com.gulimall.guliproduct.entity.AttrAttrgroupRelationEntity;
import com.gulimall.guliproduct.entity.AttrGroupEntity;
import com.gulimall.guliproduct.entity.CategoryEntity;
import com.gulimall.guliproduct.service.CategoryService;
import com.gulimall.guliproduct.vo.AttrRespVo;
import com.gulimall.guliproduct.vo.AttrVo;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.BeanUtils;
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

import com.gulimall.guliproduct.dao.AttrDao;
import com.gulimall.guliproduct.entity.AttrEntity;
import com.gulimall.guliproduct.service.AttrService;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    //注入属性分组和属性关联表的dao
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    //注入属性分组dao
    @Autowired
    AttrGroupDao attrGroupDao;

    //注入分类dao
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    //根据属性类型和分类id查询属性
    @Override
    public PageUtils queryPage(Map<String, Object> params, String type, Long catId) {
        //1.获取查询条件中的key
        String key = (String) params.get("key");
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        //attrEntityQueryWrapper.eq("attr_type",type);
        //销售类型
        if(type.equals("base")) {
            attrEntityQueryWrapper.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        }
        if (type.equals("sale")) {
            attrEntityQueryWrapper.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        }
        //2.如果key不为空
        if (!StringUtils.isEmpty(key)) {
            //key作为属性id和属性名来查询
            attrEntityQueryWrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //3.传入的三级分类id为0，就查所有的
        if (catId != 0) {
            attrEntityQueryWrapper.eq("catelog_Id", catId);
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params), //分页条件
                attrEntityQueryWrapper //查询条件
        );

        //最终要查询到返回前端的vo
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //销售属性不需要属性分组，只有是基本属性的时候才需要关联表
            if(type.equals("base")){
                //查询到属性分组名
                //1.先拿到attrid
                Long attrId = attrEntity.getAttrId();
                //1.再拿到attr attrgroup关系表中的 attrgroupid
                QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationWrapper = new QueryWrapper<>();
                attrAttrgroupRelationWrapper.eq("attr_id", attrId);
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(attrAttrgroupRelationWrapper);
                if (attrAttrgroupRelationEntity != null) {
                    //2.根据查询到attr-attrgruoprealtion对象拿到attrgroupid
                    Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                    if(attrGroupId != null){
                        //3.拿到attrGroupId，到attrGroup表中查找attrgroup
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                        //4.拿到attrgroupname
                        String attrGroupName = attrGroupEntity.getAttrGroupName();
                        attrRespVo.setGroupName(attrGroupName);
                    }

                }
            }

            //5.拿到categroyid去查询category对象
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                //6.拿到categroyname
                String categoryEntityName = categoryEntity.getName();

                attrRespVo.setCatelogName(categoryEntityName);
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public void saveAttr(AttrVo attr) {
        //1.先保存atrr信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);
        //只有基本参数类型才需要关联关系
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            //2.保存属性和属性分组的关联信息
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            //设置属性分组id
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            //设置属性id
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //1.先查询attrEntity基本信息
        AttrEntity attrEntity = baseMapper.selectById(attrId);
        //2.构造返回前端信息的对象
        AttrRespVo attrRespVo = new AttrRespVo();
        //3.赋值相同的信息
        BeanUtils.copyProperties(attrEntity,attrRespVo);
        //4.查询到前端对象所需信息 attrGroupId,catelogPath
        //只有基本属性才需要关联信息
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
            attrAttrgroupRelationEntityQueryWrapper.eq("attr_id",attrId);
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(attrAttrgroupRelationEntityQueryWrapper);
            if(attrAttrgroupRelationEntity != null){
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                //5.设置属性attrGroupId
                attrRespVo.setAttrGroupId(attrGroupId);
            }
        }

        //6.得到catelogPath
        Long[] catelogPath = categoryService.getCatelogPath(attrEntity.getCatelogId());
        if(catelogPath != null){
            //7.设置属性catelogPath
            attrRespVo.setCatelogPath(catelogPath);
        }

        return attrRespVo;
    }

    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        baseMapper.updateById(attrEntity);
        //修改关联表
        //只有基本类型属性才需要关联表
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            UpdateWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityUpdateWrapper = new UpdateWrapper<>();
            attrAttrgroupRelationEntityUpdateWrapper.eq("attr_id",relationEntity.getAttrId());

            //如果表中没有这个数据
            int count = attrAttrgroupRelationDao.selectCount(attrAttrgroupRelationEntityUpdateWrapper);
            if(count == 0){
                //没有数据就是要插入
                attrAttrgroupRelationDao.insert(relationEntity);
            }else{
                //有数据就新增
                attrAttrgroupRelationDao.update(relationEntity,attrAttrgroupRelationEntityUpdateWrapper);
            }
        }
        //如果基本属性被更改为了销售属性，要在关系表中删除这个属性,关系表只存放基本属性
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()){
            //在关系表中查找这个属性
            QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
            attrAttrgroupRelationEntityQueryWrapper.eq("attr_id",attr.getAttrId());
            int count = attrAttrgroupRelationDao.selectCount(attrAttrgroupRelationEntityQueryWrapper);
            if(count != 0){
                attrAttrgroupRelationDao.delete(attrAttrgroupRelationEntityQueryWrapper);
            }
        }
    }

    //查询到可被检索到的属性id
    @Override
    public List<Long> getSearchAttrIds(List<Long> attrIds) {
        //查找到可被检索的属性id
        return baseMapper.selectSearchAttrIds(attrIds);
    }
}