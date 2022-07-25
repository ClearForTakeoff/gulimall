package com.gulimall.guliorder.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.common.exception.NoStockException;
import com.common.to.*;
import com.common.to.mq.StockDetailTo;
import com.common.to.mq.StockLockedTo;
import com.common.utils.R;
import com.google.common.collect.Ordering;
import com.gulimall.guliorder.client.CartClient;
import com.gulimall.guliorder.client.MemberClient;
import com.gulimall.guliorder.client.ProductClient;
import com.gulimall.guliorder.client.WareClient;
import com.gulimall.guliorder.entity.OrderItemEntity;
import com.gulimall.guliorder.entity.PaymentInfoEntity;
import com.gulimall.guliorder.entity.to.OrderCreateTo;
import com.gulimall.guliorder.entity.vo.*;
import com.gulimall.guliorder.enume.OrderStatusEnum;
import com.gulimall.guliorder.interceptor.UserLoginInterceptor;
import com.gulimall.guliorder.service.OrderItemService;

import com.gulimall.guliorder.service.PaymentInfoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.gulimall.guliorder.dao.OrderDao;
import com.gulimall.guliorder.entity.OrderEntity;
import com.gulimall.guliorder.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.common.constant.OrderConstant.ORDER_TOKEN_PREFIX;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();
    @Autowired
    ProductClient productClient;
    @Autowired
    MemberClient memberClient;
    @Autowired
    CartClient  cartClient;

    @Autowired
    WareClient wareClient;
    //异步任务
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo getOrderInfo() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //异步请求涉及线程间通信，需要取得请求数据，放到线程中
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

       //收货地址列表
//        List<UserAddressVo> userAddressList;
        //根据用户id，远程查询收货地址列表
        MemberResponseVo memberResponseVo = UserLoginInterceptor.threadLocal.get();
        Long id = memberResponseVo.getId();
        //使用异步任务
        CompletableFuture<Void> address = CompletableFuture.runAsync(() -> {
            //共享请求数据
            RequestContextHolder.setRequestAttributes(attributes);
            List<UserAddressVo> memberAddress = memberClient.getMemberAddress(id);
            orderConfirmVo.setUserAddressList(memberAddress);
        }, threadPoolExecutor);

        CompletableFuture<Void> cartItem = CompletableFuture.runAsync(() -> {
            //共享请求数据
            RequestContextHolder.setRequestAttributes(attributes);
        //商品
            //远程服 务，到redis中查询保存的购物项
            List<OrderItemVo> checkedCartItem = cartClient.getCheckedCartItem();
            orderConfirmVo.setOrderItemVoList(checkedCartItem);
        }, threadPoolExecutor).thenRunAsync(()->{
            //拿到上一步查询的数据
            List<OrderItemVo> orderItemVoList = orderConfirmVo.getOrderItemVoList();
            List<Long> skuIds = orderItemVoList.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            //远程服务查询库存
            R r = wareClient.hasStock(skuIds);
            List<HasStockTo> data = r.getData(new TypeReference<List<HasStockTo>>() {
            });
            if(data !=null){
                //把库存数据封装为map
                Map<Long, Boolean> collect = data.stream().collect(Collectors.toMap(HasStockTo::getSkuId, HasStockTo::getHasStock));
                orderConfirmVo.setHasStock(collect);
            }
        });

        //会员积分
//        Integer integrations;
        Integer memberIntegration = memberResponseVo.getIntegration();
//        //订单总额
//        BigDecimal total;

//        //应付价格
//        BigDecimal payPrice;
        //设置积分之后
        orderConfirmVo.setIntegrations(memberIntegration);

        //设置防重令牌
        String token = UUID.randomUUID().toString().replace("-","");
        //把令牌分别放到redis和页面
        stringRedisTemplate.opsForValue().set(ORDER_TOKEN_PREFIX + memberResponseVo.getId(),token,15, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        //异步任务完成之后
        CompletableFuture.allOf(address,cartItem).get();
        return orderConfirmVo;
    }

    //提交订单
    //@GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        //线程共享提交的用户数据 地址id，token，支付价格
        submitVoThreadLocal.set(orderSubmitVo);
        //创建返回对象
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        //去创建、下订单、验令牌、验价格、锁定库存...

        //获取当前用户登录的信息
        MemberResponseVo memberResponseVo = UserLoginInterceptor.threadLocal.get();
        responseVo.setCode(0);

        //1、验证令牌是否合法【令牌的对比和删除必须保证原子性】
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmitVo.getOrderToken();

        //通过lure脚本原子验证令牌和删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(ORDER_TOKEN_PREFIX + memberResponseVo.getId()),
                orderToken);

        if (result == 0L) {
            //令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            //令牌验证成功
            //1、创建订单、订单项等信息
            OrderCreateTo order =  createOrder();

            //2、验证价格,页面数据回传的价格与数据库商品计算出的价格进行比较
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();

            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比
                //TODO 3、保存订单
                saveOrder(order);
                //4、库存锁定,只要有异常，回滚订单数据
                //订单号、所有订单项信息(skuId,skuNum,skuName)
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());

                //获取出要锁定的商品数据信息
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setPrice(item.getSkuPrice());
                    orderItemVo.setSkuTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(orderItemVos);

                //TODO 调用远程锁定库存的方法
                //出现的问题：扣减库存成功了，但是由于网络原因超时，出现异常，导致订单事务回滚，库存事务不回滚(解决方案：seata)
                //为了保证高并发，不推荐使用seata，因为是加锁，并行化，提升不了效率,可以发消息给库存服务
                R r = wareClient.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //锁定成功
                    responseVo.setOrder(order.getOrder());
                    // int i = 10/0;

                    //TODO 订单创建成功，发送消息给MQ
                    rabbitTemplate.convertAndSend("order.event.exchange","order.create.order",order.getOrder());

                    //删除购物车里的数据
                   // redisTemplate.delete(CART_PREFIX+memberResponseVo.getId());
                    return responseVo;
                } else {
                    //锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                    /*responseVo.setCode(3);
                    return responseVo;*/
                }

            } else {
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    //查询订单状态
    @Override
    public OrderEntity getOrderStatus(String orderSn) {
        OrderEntity order = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order;
    }


    //创建秒杀订单
    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {

        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        orderEntity.setCreateTime(new Date());
        BigDecimal totalPrice = orderTo.getSeckillPrice().multiply(BigDecimal.valueOf(orderTo.getNum()));
        orderEntity.setPayAmount(totalPrice);
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        //保存订单
        this.save(orderEntity);

        //保存订单项信息
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderSn(orderTo.getOrderSn());
        orderItem.setRealAmount(totalPrice);

        orderItem.setSkuQuantity(orderTo.getNum());

        //保存商品的spu信息
        SpuInfoTo spuInfoData = productClient.getSpuInfoBySkuId(orderTo.getSkuId());

        orderItem.setSpuId(spuInfoData.getId());
        orderItem.setSpuName(spuInfoData.getSpuName());
        orderItem.setSpuBrand(spuInfoData.getBrandName());
        orderItem.setCategoryId(spuInfoData.getCatelogId());

        //保存订单项数据
        orderItemService.save(orderItem);
    }

    //支付成功后
    @Override
    public void payFinish(String orderSn,String paymentSubject) {
        //支付成功
        OrderEntity orderStatus = this.getOrderStatus(orderSn);
        //修改订单状态为已支付
        orderStatus.setStatus(1);
        //修改支付时间
        orderStatus.setPaymentTime(new Date());
        //支付类型，1->支付宝；2->微信；3->银联； 4->货到付款；
        orderStatus.setPayType(1);
        baseMapper.updateById(orderStatus);

        //创建支付记录
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(orderSn);
        paymentInfoEntity.setOrderId(orderStatus.getId());
        paymentInfoEntity.setAlipayTradeNo(orderSn);
        paymentInfoEntity.setTotalAmount(orderStatus.getTotalAmount());
        paymentInfoEntity.setSubject(paymentSubject);

        //插入数据
        paymentInfoService.save(paymentInfoEntity);
    }

    @Override
    public PageUtils getOrderList(Map<String,Object> params) {
        MemberResponseVo memberResponseVo = UserLoginInterceptor.threadLocal.get();

        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
                        .eq("member_id",memberResponseVo.getId()).orderByDesc("create_time")
        );

        //遍历所有订单集合
        List<OrderEntity> orderEntityList = page.getRecords().stream().map(order -> {
            //根据订单号查询订单项里的数据
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>()
                    .eq("order_sn", order.getOrderSn()));
            order.setOrderItemEntityList(orderItemEntities);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(orderEntityList);

        return new PageUtils(page);
    }

    //关闭订单
    @Override
    public void closeOrder(OrderEntity orderEntity) {
        //先查询订单状态
        String orderSn = orderEntity.getOrderSn();
        QueryWrapper<OrderEntity> orderEntityQueryWrapper = new QueryWrapper<>();
        orderEntityQueryWrapper.eq("order_sn",orderSn);
        OrderEntity order = baseMapper.selectOne(orderEntityQueryWrapper);
        //当商品处于未付款时，关闭订单
        if(order.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            OrderEntity updateOrder = new OrderEntity();
            updateOrder.setOrderSn(orderSn);
            updateOrder.setStatus(OrderStatusEnum.CANCLED.getCode());
            QueryWrapper<OrderEntity> orderEntityQueryWrapper1 = new QueryWrapper<>();
            orderEntityQueryWrapper1.eq("order_sn",orderSn);
            baseMapper.update(updateOrder,orderEntityQueryWrapper1);
            //发送消息
            //转成相同的类型
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            rabbitTemplate.convertAndSend("order.event.exchange","order.release.other",orderTo);
        }

    }
    //保存订单
    private void saveOrder(OrderCreateTo order) {
        //保存订单
        this.baseMapper.insert(order.getOrder());
        //保存订单项
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }


    //封装订单的信息
    private OrderCreateTo createOrder(){
        //获取当前用户登录信息
        MemberResponseVo memberResponseVo = UserLoginInterceptor.threadLocal.get();
        //1.生成订单号
        String timeId = IdWorker.getTimeId();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(timeId);
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setMemberUsername(memberResponseVo.getUsername());
        //threadLocal拿到数据
        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();

        //得到收货地址
        R fare = wareClient.getFare(orderSubmitVo.getAddrId());
        FareVo data = fare.getData(new TypeReference<FareVo>(){});
        //2.设置收货信息
        orderEntity.setReceiverDetailAddress(data.getAddress().getDetailAddress());
        orderEntity.setReceiverProvince(data.getAddress().getProvince());
        orderEntity.setReceiverCity(data.getAddress().getCity());
        orderEntity.setReceiverRegion(data.getAddress().getRegion());
        orderEntity.setReceiverPostCode(data.getAddress().getPostCode());
        orderEntity.setReceiverName(data.getAddress().getName());
        orderEntity.setReceiverPhone(data.getAddress().getPhone());
        orderEntity.setFreightAmount(data.getFare());

        //3.设置运费
        orderEntity.setFreightAmount(data.getFare());

        //4.构建订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItemEntities(orderEntity.getOrderSn());

        //5.设置订单状态信息
        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        //设置订单的价格信息
        computeOrderPrice(orderEntity,orderItemEntities);

        //创建订单前端对象
        OrderCreateTo createTo = new OrderCreateTo();
        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);
        return  createTo;
    }

    //设置订单的价格信息
    private void computeOrderPrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());

            //总价
            total = total.add(orderItem.getRealAmount());

            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();

        }
        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);

        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);
    }


    //构建订单项数组
    private List<OrderItemEntity> buildOrderItemEntities(String orderSn) {
        //远程服务查询购物车中选中的购物车项，查询到的是数据库中最新的价格
        List<OrderItemVo> checkedCartItem = cartClient.getCheckedCartItem();
        //转换为OrderItemEntity
        if(checkedCartItem != null && checkedCartItem.size() > 0){
           return  checkedCartItem.stream().map((item -> {
                //需要商品的详细信息
                OrderItemEntity orderItemEntity = buildOrderItemEntity(item);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            })).collect(Collectors.toList());
        }
        return null;
    }

    //构建单个订单项
    private OrderItemEntity buildOrderItemEntity(OrderItemVo item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1.订单信息
        //方法返回后设置orderSn
        //2.spu信息 远程查询
        SpuInfoTo spuInfoBySkuId = productClient.getSpuInfoBySkuId(item.getSkuId());
        orderItemEntity.setSpuId(spuInfoBySkuId.getId());
        orderItemEntity.setSpuName(spuInfoBySkuId.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoBySkuId.getBrandName());
        //3.sku信息 （orderItemVo中已有）
        orderItemEntity.setSkuId(item.getSkuId());
        orderItemEntity.setSkuName(item.getSkuTitle());
        orderItemEntity.setSkuPic(item.getDefaultImg());
        orderItemEntity.setSkuQuantity(item.getCount());
        orderItemEntity.setSkuPrice(item.getPrice());
        String s = StringUtils.collectionToDelimitedString(item.getAttrs(), ";");
        orderItemEntity.setSkuAttrsVals(s);


        //4、商品的积分信息
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());

        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }


}