package com.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author:admin
 * date:2022/6/2
 * Info:
 **/

@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;

}