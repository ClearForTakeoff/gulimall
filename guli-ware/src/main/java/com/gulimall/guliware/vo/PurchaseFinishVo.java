package com.gulimall.guliware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/8
 * @Description:
 **/
@Data
public class PurchaseFinishVo {
    private Long id;
    private List<PurchaseItemFinishVo> items;
}
