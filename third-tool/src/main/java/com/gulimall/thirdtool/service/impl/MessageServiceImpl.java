package com.gulimall.thirdtool.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.gulimall.thirdtool.service.MessageService;
import com.gulimall.thirdtool.utils.RandomUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: duhang
 * @Date: 2022/6/30
 * @Description:
 **/
@Service
public class MessageServiceImpl implements MessageService {
    //引入redis防止重复刷新
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public Boolean sendMessage(String phoneNumber) throws Exception {
        //TODO 防止60内再次请求发送
        //从redis中获取，没有就生成验证码
        String redisCode = redisTemplate.opsForValue().get("sms_code:"+phoneNumber);
        if(!StringUtils.isEmpty(redisCode)) {
            //从redisCode中分割出时间
            Long time = Long.valueOf(redisCode.split("_")[1]);
            if(System.currentTimeMillis() - time < 60 * 1000){ //时间少于1分钟不发送
                return false;
            }
        }

        //时间长于1分钟，再次生成验证码，存入redis，再发送
        //工具类生成随机验证码,
        String code = RandomUtil.getSixBitRandom();
        //验证码带上生成的时间,存入redis
        String redisSaveCode = code +"_"+System.currentTimeMillis();
        //执行发送验证码
        boolean isSuccess = sendCode(phoneNumber, code);
        if(isSuccess){ //如果发送成功才存redis
            //验证码存入redis，并设置有效时间
            redisTemplate.opsForValue().set("sms_code:"+phoneNumber,redisSaveCode,15, TimeUnit.MINUTES);
        }
        return isSuccess;

    }
    public boolean sendCode(String phoneNumber,String code) throws Exception {
        if(phoneNumber == null) return false;
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId("LTAI5tEvfAN9rsCoZUTxuJvT")
                // 您的AccessKey Secret
                .setAccessKeySecret("x31a7zi3n6aFMFm9E0jmxhfs75iinX");
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        com.aliyun.dysmsapi20170525.Client client =   new Client(config);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("阿里云短信测试")//短信签名
                .setTemplateCode("SMS_154950909")//短信模板
                .setPhoneNumbers(phoneNumber)
                .setTemplateParam("{\"code\":\""+code+"\"}");
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        SendSmsResponseBody body = sendSmsResponse.getBody();
        String responseCode = body.getCode();
        System.out.println(body.getMessage());
        if(responseCode.equals("OK")){
            return true;
        }else{
            return false;
        }
    }
}
