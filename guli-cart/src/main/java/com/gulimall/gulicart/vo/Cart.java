package com.gulimall.gulicart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description: 购物车数据模型
 **/
public class Cart {

    private List<CartItem> itemList;
    private Integer count; //总数
    private Integer countType;//类型数量
    private BigDecimal totalPrice;//总价
    private BigDecimal reduce = new BigDecimal(0) ;//优惠的价格

    public List<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<CartItem> itemList) {
        this.itemList = itemList;
    }

    public Integer getCount() {
        //获取商品总数
        this.count = 0;
        if(this.itemList != null && this.itemList.size() > 0){
            for (CartItem cartItem : this.itemList) {
                this.count += cartItem.getCount();
            }
        }
        return this.count;
    }



    public Integer getCountType() {
        this.countType = itemList.size();
        return countType;
    }



    public BigDecimal getTotalPrice() {
        totalPrice = new BigDecimal(0);
        if(itemList != null && itemList.size() > 0){
            for (CartItem cartItem : itemList) {
                if(cartItem.isChecked()){
                    BigDecimal totalPrice1 = cartItem.getTotalPrice();
                    totalPrice = totalPrice.add(totalPrice1);
                }
            }
        }
        //减去优惠的价格
        totalPrice = totalPrice.subtract(reduce);
        return totalPrice;
    }



    public BigDecimal getReduce() {
        return reduce;
    }


}
