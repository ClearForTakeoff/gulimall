package com.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;

/**
 * author:admin
 * date:2022/5/19
 * Info: 自定义校验注解的校验器,实现结构的泛型第一个是校验器校验的注解，第二个是校验参数的类型
 **/


public class ZeroOrOneValueConstraintValidator implements ConstraintValidator<ZeroOrOneValue,Integer> {
    private HashSet<Integer> set = new HashSet();
    //初始化
    @Override
    public void initialize(ZeroOrOneValue constraintAnnotation) {
        //取到注解中传入的数组
        int[] values = constraintAnnotation.vals();
        for (int value : values) {
            set.add(value);
        }
    }

    //进行校验的方法
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        //跟请求中传来的数据进行
        return set.contains(integer);
    }
}
