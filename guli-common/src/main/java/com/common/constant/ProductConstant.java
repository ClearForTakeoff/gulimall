package com.common.constant;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * author:admin
 * date:2022/5/25
 * Info:
 **/


public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String message;

        AttrEnum(int code,String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum SpuPublishStatus{
        SPU_CREATED(0,"新建spu"),
        SPU_UP(1,"spu上架"),
        SPU_DOWN(2,"spu下架");

        private Integer status;
        private String msg;

        SpuPublishStatus(Integer status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public Integer getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }
    }
}
