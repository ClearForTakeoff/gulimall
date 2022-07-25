package com.common.exception;

/**
 * @Author: duhang
 * @Date: 2022/7/9
 * @Description:
 **/
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId) {
        super("商品："+skuId+",没有足够库存了");
    }

    public NoStockException(String message) {
        super(message);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
