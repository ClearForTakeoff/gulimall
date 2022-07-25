package com.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

/**
 * author:admin
 * date:2022/5/19
 * Info: 自定义校验注解
 **/

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {ZeroOrOneValueConstraintValidator.class} //将校验注解关联到自定义校验器
)
public @interface ZeroOrOneValue {
    String message() default "{com.common.validator.ZeroOrOneValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {};
}
