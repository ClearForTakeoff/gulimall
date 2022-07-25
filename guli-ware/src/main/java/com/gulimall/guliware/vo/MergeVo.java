package com.gulimall.guliware.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * author:admin
 * date:2022/6/3
 * Info: 用于合并采购需求封装提交到后端的数据
 **/

@Data
public class MergeVo {
    private Long purchaseId; //采购单的Id
    private List<Long> items;//采购需求id
}
