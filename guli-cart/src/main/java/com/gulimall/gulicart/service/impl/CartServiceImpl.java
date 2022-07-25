package com.gulimall.gulicart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.gulimall.gulicart.feign.ProductFeignClient;
import com.gulimall.gulicart.interceptor.CartInterceptor;
import com.gulimall.gulicart.service.CartService;
import com.gulimall.gulicart.vo.Cart;
import com.gulimall.gulicart.vo.CartItem;
import com.gulimall.gulicart.vo.SkuInfoVo;
import com.gulimall.gulicart.vo.UserInfo;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description:
 **/
@Service
public class CartServiceImpl implements CartService {
    private final static String CART_PREFIX = "guliamll:cart:" ;

    //线程池
    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    //添加商品到购物车
    @Override
    public CartItem addToCart(Long skuId, int count) throws ExecutionException, InterruptedException {
        //得到redis中对购物车key的操作
        BoundHashOperations<String, Object, Object> operations = cartOps();

        //先从redis取出数据
        String redisSku = (String) operations.get(skuId.toString());
        //如果没有这个商品
        if(StringUtils.isEmpty(redisSku)){

            CartItem cartItem = new CartItem();
            //异步任务1
            CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
                //远程服务查询skuInfo
                R info = productFeignClient.info(skuId);
                SkuInfoVo skuInfo = info.getDataByName("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //封装购物项

                cartItem.setSkuId(skuId);
                cartItem.setSkuTitle(skuInfo.getSkuTitle());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setCount(count);
                cartItem.setDefaultImg(skuInfo.getSkuDefaultImg());
            }, executor);

            //异步任务2
            CompletableFuture<Void> attrs = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttr = productFeignClient.getSkuSaleAttr(skuId);
                cartItem.setAttrs(skuSaleAttr);
            }, executor);
            //等异步任务都完成
            CompletableFuture.allOf(attrs,runAsync).get();
            String jsonString = JSON.toJSONString(cartItem);
            operations.put(skuId.toString(),jsonString);
            return cartItem;
        }else{
            //如果有这个商品,更新数量
            CartItem cartItemExisted = JSON.parseObject(redisSku, CartItem.class);
            cartItemExisted.setCount(cartItemExisted.getCount() + count);
            //更新redis
            String jsonString = JSON.toJSONString(cartItemExisted);
            operations.put(skuId.toString(),jsonString);
            return cartItemExisted;
        }
    }


    //查询添加的商品
    @Override
    public CartItem getAddCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> operations = cartOps();
        String addCartItem = (String) operations.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(addCartItem, CartItem.class);
        return cartItem;
    }

    private BoundHashOperations<String, Object, Object> cartOps() {
        //得到用户信息，判断是否登录
        String cartKey;
        UserInfo userInfo = CartInterceptor.threadLocal.get();
        if(userInfo.getUserId() != null){ //已经登陆过
            cartKey = CART_PREFIX + userInfo.getUserId();
        }else{ //没有登录
            //临沭购物车
            cartKey = CART_PREFIX + userInfo.getUserKey();
        }

        //
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        return operations;
    }

    //获取购物车信息
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        //获取用户信息
        UserInfo userInfo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        //拿到用户id和临时购物车user-key
        String userKey = userInfo.getUserKey();
        Long userId = userInfo.getUserId();

        //查询临时购物车
        String cartKey = CART_PREFIX + userKey;
        List<CartItem> cartItems = getCartItem(cartKey);

        //查询登录购物车
        if(userId != null){ //已经登录状态
            String cartKeyLogin =  CART_PREFIX + userId;
            ///合并临时购物车与登录购物车
            if(cartItems != null && cartItems.size() > 0){
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(),cartItem.getCount());
                }
                //清空临时购物车
                clearCartInfo(cartKey);
            }
            List<CartItem> cartItemLongIn = getCartItem(cartKeyLogin);
            cart.setItemList(cartItemLongIn);

        }else{//没登录时添加
            cart.setItemList(cartItems);
        }

        return cart;
    }

    //获取购物车的item
    public List<CartItem> getCartItem(String cartKey){
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if(values != null && values.size() > 0 ){
            //每object个购物车项转换为
            return values.stream().map((item) -> {
                String s = item.toString();
                return JSON.parseObject(s, CartItem.class);

            }).collect(Collectors.toList());
        }
        return null;
    }

    //查询购物车选中的购物项
    @Override
    public List<CartItem> getCheckedCartItem() {
        UserInfo userInfo = CartInterceptor.threadLocal.get();
        if(userInfo.getUserId() != null) { //已登录
            Long userId = userInfo.getUserId();
            String cartKeyLogin =  CART_PREFIX + userId;
            List<CartItem> cartItems = getCartItem(cartKeyLogin);
            if(cartItems != null && cartItems.size() > 0){
                //获取被选中的购物项
                return  cartItems.stream().filter(CartItem::isChecked)
                        .map(item->{
                            //查询到最新的item的价格
                            //远程查询
                            BigDecimal skuPrice = productFeignClient.getSkuPrice(item.getSkuId());
                            item.setPrice(skuPrice);
                            return item;
                        }).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public void clearCartInfo(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> operations = cartOps();
        //查询到需要修改的购物项
        CartItem addCartItem = getAddCartItem(skuId);
        //修改选中属性
        addCartItem.setChecked(check == 1);
        String jsonString = JSON.toJSONString(addCartItem);
        //更新到redis
        operations.put(skuId.toString(),jsonString);
    }

    //修改购物车商品数量
    @Override
    public void editCartItemCount(Long skuId, Integer count) {
        BoundHashOperations<String, Object, Object> operations = cartOps();
        //查询到需要修改的购物项
        CartItem addCartItem = getAddCartItem(skuId); //从redis中获取到数据
        //修改属性
        addCartItem.setCount(count);
        String jsonString = JSON.toJSONString(addCartItem);
        //更新到redis
        operations.put(skuId.toString(),jsonString);
    }


    //删除购物车商品
    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> operations = cartOps();
        operations.delete(skuId.toString());
    }
}
