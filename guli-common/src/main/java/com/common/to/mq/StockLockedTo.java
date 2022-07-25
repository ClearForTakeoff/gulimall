package com.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/7/10
 * @Description: 锁库存消息的To
 **/
@Data
public class StockLockedTo {
    private Long id; //库存工作单id
    /** 工作单详情的所有信息 **/
    private StockDetailTo detailTo;

}
