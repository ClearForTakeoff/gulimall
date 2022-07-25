package com.gulimall.guliproduct.exception;

import com.common.exception.BizCodeEnum;
import com.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * author:admin
 * date:2022/5/19
 * Info:
 **/

@RestControllerAdvice(basePackages = "com.gulimall.guliproduct.controller")
public class ControllerAdviceException {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerNotValidException(MethodArgumentNotValidException exception){
        BindingResult result = exception.getBindingResult();
        HashMap<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            String message = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            errorMap.put(field, message);
        }
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }
}
