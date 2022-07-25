package com.gulimall.guliware.vo;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @Author: duhang
 * @Date: 2022/6/8
 * @Description:  完成采购后上传到后端的采购需求数据
 **/

@Data
public class PurchaseItemFinishVo {
    private Long purchaseItemId;
    private Integer status;
    private String msg;
}
