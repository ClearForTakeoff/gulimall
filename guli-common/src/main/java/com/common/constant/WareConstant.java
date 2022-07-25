package com.common.constant;

/**
 * @Author: duhang
 * @Date: 2022/6/7
 * @Description:
 **/
public class WareConstant {
    public enum PurchaseStatusEnum{
        PURCHASE_CREATE(0,"新建"),
        PURCHASE_ASSIGNED(1,"已分配"),
        PURCHASE_RECEIVED(2,"已领取"),
        PURCHASE_FINISHED(3,"已完成"),
        PURCHASE_HASERROR(4,"有异常");


        private int code;
        private String msg;
        PurchaseStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum PurchaseDetailStatusEnum{
        PURCHASE_DETAIL_CREATE(0,"新建"),
        PURCHASE_DETAIL_ASSIGNED(1,"已分配"),
        PURCHASE_DETAIL_HANDLING(2,"正在采购"),
        PURCHASE_DETAIL_FINISHED(3,"已完成"),
        PURCHASE_DETAIL_HASERROR(4,"有异常");


        private int code;
        private String msg;
        PurchaseDetailStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
