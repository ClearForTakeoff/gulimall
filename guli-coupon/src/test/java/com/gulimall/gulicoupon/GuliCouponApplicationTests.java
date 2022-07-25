package com.gulimall.gulicoupon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
class GuliCouponApplicationTests {

    @Test
    void contextLoads() {

        //计算三天的日期
        //得到今天的日期和三天后的日期
        //其实时间 当天的 00:00:00，结束时间三天后的 23:59:59
        LocalDate today = LocalDate.now();
        LocalDate threeDayLater = today.plusDays(2); //两天后的日期
        //得到时分秒
        LocalTime min = LocalTime.MIN; //最小时间
        LocalTime max = LocalTime.MAX; //最大时间
        //构造日期时间
        LocalDateTime todayTime = LocalDateTime.of(today, min);
        LocalDateTime threeDay = LocalDateTime.of(threeDayLater, max);
        //格式化
        String start = todayTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = threeDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(start + "-------" + end);
    }

}
