package com.gulimall.guliproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.gulimall.guliproduct.service.CategoryBrandRelationService;
import com.gulimall.guliproduct.vo.front.TwoCategoryVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliproduct.dao.CategoryDao;
import com.gulimall.guliproduct.entity.CategoryEntity;
import com.gulimall.guliproduct.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;



@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    //注入redisTemplate,存放json数据
    @Autowired
    StringRedisTemplate redisTemplate;

    //redisson客户端
    @Autowired
    RedissonClient redissonClient;
    //注入品牌分类关系表mapper
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    //生成树形结构
    @Override
    public List<CategoryEntity> listAsTree() {
        //1.查出所有分类
        List<CategoryEntity> categoryEntitiesAll = baseMapper.selectList(null);
        //2.查出所有一级分类
        //filter 对 查询list转为的流进行筛选，
        List<CategoryEntity> onLevelCategory = categoryEntitiesAll.stream()
                .filter(categoryEntity -> categoryEntity.getCatLevel() == 1)
                //map对流的数据进行遍历操作
                //遍历每一个对象为其children属性赋值
                .map((menu) ->{
                    menu.setChildren(getChildren(menu,categoryEntitiesAll));
                    return menu;
                })
                //返回menu为其进行排序
                .sorted((menu1,menu2)->{
                    return menu1.getSort() - menu2.getSort();
                })
                .collect(Collectors.toList());

        return onLevelCategory;
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> categoryEntitiesAll) {
       //筛选children的子分类
        List<CategoryEntity> children = categoryEntitiesAll.stream()
                .filter(categoryEntity -> Objects.equals(root.getCatId(), categoryEntity.getParentCid()))
                //对每一个子分类递归设置子分类
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, categoryEntitiesAll));
                    return categoryEntity;
                })
                //返回menu为其进行排序
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0:menu1.getSort()) - ( menu2.getSort() == null ? 0:menu2.getSort());
                })
                .collect(Collectors.toList());

        return children;
    }

    //删除分类菜单的方法
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查此分类菜单是否在别处被引用
        baseMapper.deleteBatchIds(asList);
    }

    //根据三级分类id查询所有父分类id
    @Override
    public Long[] getCatelogPath(Long catelogId) {

        ArrayList<Long> longs = new ArrayList<>();
        //递归查找
        recurison(longs,catelogId);
        return longs.toArray(new Long[longs.size()]);
    }

    //当分类信息更新后，要同时更新关联表的数据
    @Transactional
    @Override
    //数据更新之后，删除缓存数据
    //@CacheEvict(value = "category",key = "'getOneLevelCategory'")
    //组合多个缓存操作
//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'getOneLevelCategory'"),
//            @CacheEvict(value = "category",key = "'getCategoryJson'")
//    })
    @CacheEvict(value = "category",allEntries = true)//可以删除分区内所有的数据，可以用来删除相同分类的，相同类型数据，可以放在同一分区
    public void updateRelatedTable(CategoryEntity category) {
        //1.先更新自己表中的数据
        baseMapper.updateById(category);
        //2.更新品牌分类关联表数据
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
        //TODO 其他表数据更新
    }



    private void recurison(ArrayList<Long> longs, Long catelogId) {
        //查找到第一级id的父id为0，直接返回
        if(catelogId == 0){
            return;
        }
        //根据id查找分类
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        //把分类的父id带入递归
        recurison(longs,categoryEntity.getParentCid());
        //递归返回后加入id
        longs.add(catelogId);
    }


    //查询一级分类

    @Override
    @Cacheable(value = "category",key ="#root.methodName")//指定缓存分区，查询结果放入缓存，如果缓存中有，直接从缓存中返回数据
    public List<CategoryEntity> getOneLevelCategory() {
        System.out.println("查询分类");
        List<CategoryEntity> parent_cid = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return  parent_cid;
    }



    //整合springcache，只实现查询业务，放入缓存和分布式锁操作springcache管理
    @Override
    @Cacheable(value = "category",key = "#root.methodName")
    public Map<String, List<TwoCategoryVo>> getCategoryJson() {
        System.out.println("查询数据库。。。");

        //拿到所有的分类,只查一次数据库
        List<CategoryEntity> allCategorys = this.list();

        //拿到一级分类
        List<CategoryEntity> oneLevelCategory = getSonLevelCategorys(allCategorys,0L);
        //封装数据为map，key是一级分类id，value是子分类数组
        Map<String, List<TwoCategoryVo>> collect = oneLevelCategory.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), item -> {

            //查询到所属的二级分类
            List<CategoryEntity> twoLevelCategorys = getSonLevelCategorys(allCategorys, item.getCatId());
            List<TwoCategoryVo> twoCategoryVoList = null;
            if (twoLevelCategorys != null) {
                //遍历二级分类
                twoCategoryVoList = twoLevelCategorys.stream().map(entity -> {
                    TwoCategoryVo twoCategoryVo = new TwoCategoryVo();
                    twoCategoryVo.setCatalog1Id(item.getCatId().toString());
                    twoCategoryVo.setId(entity.getCatId().toString());
                    twoCategoryVo.setName(entity.getName());

                    //查这个二级分类的三级分类
                    List<CategoryEntity> threeLevelCategorys = getSonLevelCategorys(allCategorys, entity.getCatId());
                    List<TwoCategoryVo.ThreeCategoryVo> catalog3List = null;
                    if(threeLevelCategorys != null) {
                        //封装三级分类list
                        catalog3List = threeLevelCategorys.stream().map(categoryEntity -> {
                            TwoCategoryVo.ThreeCategoryVo threeCategoryVo = new TwoCategoryVo.ThreeCategoryVo();
                            threeCategoryVo.setName(categoryEntity.getName());
                            threeCategoryVo.setId(categoryEntity.getCatId().toString());
                            threeCategoryVo.setCatalog2Id(entity.getCatId().toString());
                            return threeCategoryVo;
                        }).collect(Collectors.toList());
                    }
                    twoCategoryVo.setCatalog3List(catalog3List);
                    return twoCategoryVo;
                }).collect(Collectors.toList());
            }
            return twoCategoryVoList;
        }));
        return collect;
    }


    //整合redis和分布式锁，将查询数据放入缓存，从redis缓存中获取分类
    public Map<String, List<TwoCategoryVo>> getCategoryJsonRedis() {
        /*
        1.返回空结果
        2.设置随机的过期时间
        3.查数据库时加锁
         */
        //1.先从缓存中获取
        String catalogsJson = redisTemplate.opsForValue().get("catalogs");
        //如果为空表示缓存中没有数据
        if(StringUtils.isEmpty(catalogsJson)){
            //2.从数据库查询，放入缓存
            //System.out.println("缓存没有命中>>>>>>>查询数据库");
            Map<String, List<TwoCategoryVo>> categoryJsonFromDB = getCategoryJsonFromDBWithRedisLock();
            //直接返回数据库查询的数据
            return categoryJsonFromDB;
        }
        //
        System.out.println("缓存命中>>>>>>>>");
        //3.缓存命中，缓存数据json字符串转换为Map直接返回
        Map<String, List<TwoCategoryVo>> map = JSON.parseObject(catalogsJson, new TypeReference<Map<String, List<TwoCategoryVo>>>() {
        });
        return map;
    }

    //redisson分布式锁
    /*
    缓存一致性：修改数据库之后，缓存数据变脏，应该更新
   缓存数据加上过期时间，出发主动更新
   数据操作加上分布式读写锁，
     */
    public  Map<String, List<TwoCategoryVo>> getCategoryJsonFromDBWithRedissonLock() {
        //占分布式锁
        RLock categoryLock = redissonClient.getLock("categoryJson-Lock");
        categoryLock.lock();//加锁
        Map<String, List<TwoCategoryVo>> categoryJsonFromDB = null;
        try{
            categoryJsonFromDB = getCategoryJsonFromDB();
        }finally {
            //释放锁
            categoryLock.unlock();
        }

        return categoryJsonFromDB;
    }


    //采用分布式锁的方式查询数据库
    public  Map<String, List<TwoCategoryVo>> getCategoryJsonFromDBWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        //1.向redis占分布式锁,避免死锁，在加锁的同时设置过期时间
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,100,TimeUnit.SECONDS);
        //2.如果加锁成功,执行数据库查询
        if(lock){
            System.out.println("设置分布式锁成功。。。。。。");
            //redisTemplate.expire("lock",20,TimeUnit.SECONDS);
            Map<String, List<TwoCategoryVo>> categoryJsonFromDB = null;
            //执行数据库查询
            try{
                categoryJsonFromDB = getCategoryJsonFromDB();
            }finally {

                //解锁.没有运行，会造成死锁
                //需要给锁设置过期时间
                //如果业务运行到删除锁的时候，设置的锁已经过期了，再执行删锁会删掉别的线程的锁
                //先拿到锁，判断是当前线程自己的再删
//            String lock1 = redisTemplate.opsForValue().get("lock");
//            if( uuid.equals(lock1)){
//                redisTemplate.delete("lock");
//            }
                //删除锁原子操作，在redis中判断并删除
                //无需从redis取回来再判断,
                String delScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                //redis执行脚本，原子删除锁
                redisTemplate.execute(new DefaultRedisScript<Integer>(delScript), Arrays.asList("lock"), uuid);
            }
            return categoryJsonFromDB;
        }else{
            System.out.println("设置分布式锁失败........");
            //加锁失败，等待重试
            //等待一段时间
            return getCategoryJsonFromDBWithRedisLock();
        }

    }

    //从数据库查询分类数据
    //
    public  Map<String, List<TwoCategoryVo>> getCategoryJsonFromDB() {
        //再从缓存中查看是否有数据
        String catalogsJson = redisTemplate.opsForValue().get("catalogs");
        //如果缓存中有数据
        if(!StringUtils.isEmpty(catalogsJson)){
            Map<String, List<TwoCategoryVo>> map = JSON.parseObject(catalogsJson, new TypeReference<Map<String, List<TwoCategoryVo>>>() {
            });
            return map;
        }

        System.out.println("查询数据库。。。");

        //拿到所有的分类,只查一次数据库
        List<CategoryEntity> allCategorys = this.list();

        //拿到一级分类
        List<CategoryEntity> oneLevelCategory = getSonLevelCategorys(allCategorys,0L);
        //封装数据为map，key是一级分类id，value是子分类数组
        Map<String, List<TwoCategoryVo>> collect = oneLevelCategory.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), item -> {

            //查询到所属的二级分类
            List<CategoryEntity> twoLevelCategorys = getSonLevelCategorys(allCategorys, item.getCatId());
            List<TwoCategoryVo> twoCategoryVoList = null;
            if (twoLevelCategorys != null) {
                //遍历二级分类
                 twoCategoryVoList = twoLevelCategorys.stream().map(entity -> {
                    TwoCategoryVo twoCategoryVo = new TwoCategoryVo();
                    twoCategoryVo.setCatalog1Id(item.getCatId().toString());
                    twoCategoryVo.setId(entity.getCatId().toString());
                    twoCategoryVo.setName(entity.getName());

                    //查这个二级分类的三级分类
                    List<CategoryEntity> threeLevelCategorys = getSonLevelCategorys(allCategorys, entity.getCatId());
                     List<TwoCategoryVo.ThreeCategoryVo> catalog3List = null;
                     if(threeLevelCategorys != null) {
                         //封装三级分类list
                         catalog3List = threeLevelCategorys.stream().map(categoryEntity -> {
                             TwoCategoryVo.ThreeCategoryVo threeCategoryVo = new TwoCategoryVo.ThreeCategoryVo();
                             threeCategoryVo.setName(categoryEntity.getName());
                             threeCategoryVo.setId(categoryEntity.getCatId().toString());
                             threeCategoryVo.setCatalog2Id(entity.getCatId().toString());
                             return threeCategoryVo;
                         }).collect(Collectors.toList());
                     }
                    twoCategoryVo.setCatalog3List(catalog3List);
                    return twoCategoryVo;
                }).collect(Collectors.toList());
            }
            return twoCategoryVoList;
        }));
        //把数据库查询的数据转为json，并放入缓存
        String jsonString = JSON.toJSONString(collect);
        //把数据库查询的数据转为json字符串放入redis
        //设置过期时间为1天
        redisTemplate.opsForValue().set("catalogs", jsonString,1, TimeUnit.DAYS);
        return collect;
    }

    //抽取方法，查询指定id的子分类
    public List<CategoryEntity> getSonLevelCategorys(List<CategoryEntity> categorys, Long parentCid){
        return categorys.stream().filter(twoLevelCategory -> twoLevelCategory.getParentCid() == parentCid).collect(Collectors.toList());
    }

}