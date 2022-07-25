package com.gulimall.gulicoupon.service.impl;


import com.gulimall.gulicoupon.entity.SeckillSkuRelationEntity;
import com.gulimall.gulicoupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.gulicoupon.dao.SeckillSessionDao;
import com.gulimall.gulicoupon.entity.SeckillSessionEntity;
import com.gulimall.gulicoupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    //查询三天内的秒杀商品
    @Override
    public List<SeckillSessionEntity> getThreeDaysSeckill() {
        List<String> time = getDateTime();
        QueryWrapper<SeckillSessionEntity> wrapper = new QueryWrapper<>();
        wrapper.between("start_time",time.get(0),time.get(1));
        List<SeckillSessionEntity> list = this.list(wrapper);
        //返回秒杀商品
        List<SeckillSessionEntity> sessions = list.stream().map(session -> {
            //根据id取到关联数据
            Long id = session.getId();
            //找到秒杀关联的商品关联信息
            List<SeckillSkuRelationEntity> promotion_session_id = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
            session.setRelationEntities(promotion_session_id);
            return session;
        }).collect(Collectors.toList());
        return sessions;
    }

    public List<String> getDateTime(){
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
        ArrayList<String> localDateTimes = new ArrayList<>();
        localDateTimes.add(start);
        localDateTimes.add(end);
        return localDateTimes;
    }

}