package com.gulimall.guliware.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description:
 **/

public class OrderItemVo {
    private Long skuId;
    private String skuTitle;
    private String defaultImg;
    private boolean checked = true;
    private BigDecimal price;
    private Integer count;
    //商品属性
    private List<String> attrs;

    private BigDecimal totalPrice;

    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }

    boolean hasStock  = true;

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle;
    }

    public String getDefaultImg() {
        return defaultImg;
    }

    public void setDefaultImg(String defaultImg) {
        this.defaultImg = defaultImg;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice = this.price.multiply(new BigDecimal(this.count));
    }



    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
