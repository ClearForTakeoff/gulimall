package com.gulimall.gulicart.service;

import com.gulimall.gulicart.vo.Cart;
import com.gulimall.gulicart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description:
 **/
public interface CartService {
    //添加商品到购物车
    CartItem addToCart(Long skuId, int count) throws ExecutionException, InterruptedException;

    //查出 添加到购物车的商品
    CartItem getAddCartItem(Long skuId);

    //查询购物车信息
    Cart getCart() throws ExecutionException, InterruptedException;



    /**
     * 清空购物车的数据
     * @param cartKey
     */
     void clearCartInfo(String cartKey);

     //勾选购物项
    void checkItem(Long skuId, Integer check);

    //修改购物车商品数量
    void editCartItemCount(Long skuId, Integer count);

    //删除购物车商品
    void deleteItem(Long skuId);

    //查询选中的购物项
    List<CartItem> getCheckedCartItem();
}
