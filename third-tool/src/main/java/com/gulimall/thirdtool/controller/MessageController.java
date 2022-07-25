package com.gulimall.thirdtool.controller;

import com.common.exception.BizCodeEnum;
import com.common.utils.R;
import com.gulimall.thirdtool.service.MessageService;
import com.gulimall.thirdtool.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description:
 **/

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/send/{phoneNumber}")
    public R sendMessage(@PathVariable("phoneNumber") String phoneNumber){

        Boolean send = null;
        try {
            send = messageService.sendMessage(phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(send){
            return R.ok();
        }else{
            //返回false表示发送失败
            return R.error(BizCodeEnum.MESSAGE_SEND_EXCEPTION.getCode(), BizCodeEnum.MESSAGE_SEND_EXCEPTION.getMsg());
        }
    }

}
