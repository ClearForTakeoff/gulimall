package com.gulimall.guliorder.entity.vo;

import com.common.to.HasStockTo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description: 订单确认数据
 **/
@Data
public class OrderConfirmVo {

    //收货地址列表
    @Getter
    @Setter
    List<UserAddressVo> userAddressList;
    //商品
    @Getter
    @Setter
    List<OrderItemVo> orderItemVoList;

    //会员积分
    @Getter
    @Setter
    Integer integrations;

    //订单总额
    BigDecimal total;

    //库存信息
    Map<Long,Boolean> hasStock;

    //防重令牌
    String orderToken;
    //应付价格
    @Getter
    @Setter
    BigDecimal payPrice;

    public BigDecimal getTotal() {
        this.total = new BigDecimal(0);
        for (OrderItemVo orderItemVo : this.orderItemVoList) {
            total = total.add(orderItemVo.getTotalPrice());
        }
        return this.total;
    }

    public BigDecimal getPayPrice() {
        payPrice = total;
        return payPrice;
    }

}
