package com.gulimall.guliorder.entity.vo;

import lombok.Data;

/**
 * @Author: duhang
 * @Date: 2022/7/7
 * @Description: 用户收货地址的vo
 **/
@Data
public class UserAddressVo{
        private Long id;
        /**
         * member_id
         */
        private Long memberId;
        /**
         * 收货人姓名
         */
        private String name;
        /**
         * 电话
         */
        private String phone;
        /**
         * 邮政编码
         */
        private String postCode;
        /**
         * 省份/直辖市
         */
        private String province;
        /**
         * 城市
         */
        private String city;
        /**
         * 区
         */
        private String region;
        /**
         * 详细地址(街道)
         */
        private String detailAddress;
        /**
         * 省市区代码
         */
        private String areacode;
        /**
         * 是否默认
         */
        private Integer defaultStatus;

}
