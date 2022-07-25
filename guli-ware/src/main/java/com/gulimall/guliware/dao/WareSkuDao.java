package com.gulimall.guliware.dao;

import com.common.to.HasStockTo;
import com.gulimall.guliware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:50:27
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    //根据id查询sku是否有库存
    //Param标注sql中的参数名
    Long selectSkuHasStock(@Param("skuId") Long skuId);

    //查询那个仓库有库存
    List<Long> listWareIdHasSkuStock(@Param("skuId")Long skuId);

    //锁定库存
    Long lockSkuStock(@Param("skuId")Long skuId,@Param("wareId") Long wareId,@Param("num") Integer num);

    //解锁库存
    void unlockStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    /**
     * 添加入库信息
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

}
