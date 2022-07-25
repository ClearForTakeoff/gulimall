package com.gulimall.guliproduct.vo.front;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: duhang
 * @Date: 2022/6/15
 * @Description: 封装前端的二级分类对象
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoCategoryVo {
    private String catalog1Id;
    private String id;
    private String name;
    private List<ThreeCategoryVo> catalog3List;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreeCategoryVo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
