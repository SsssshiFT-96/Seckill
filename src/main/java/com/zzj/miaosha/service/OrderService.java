package com.zzj.miaosha.service;

import com.zzj.miaosha.dao.OrderDao;
import com.zzj.miaosha.domain.MiaoShaOrder;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.domain.OrderInfo;
import com.zzj.miaosha.redis.OrderKey;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, long goodsId) {
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        //判断是否秒杀，不从数据库中查，从redis中查
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid,
                ""+userId+""+goodsId, MiaoShaOrder.class);

    }

    @Transactional
    public OrderInfo
    createOrder(MiaoShaUser miaoShaUser, GoodsVo goods) {
        //下订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(miaoShaUser.getId());
        orderDao.insert(orderInfo);//这里修改一下

        //下秒杀订单
        MiaoShaOrder miaoShaOrder = new MiaoShaOrder();
        miaoShaOrder.setGoodsId(goods.getId());
        miaoShaOrder.setUserId(miaoShaUser.getId());
        miaoShaOrder.setOrderId(orderInfo.getId());//订单id应该是从订单信息里获取
        orderDao.insertMiaoshaOrder(miaoShaOrder);
        //将秒杀订单写入缓存
        redisService.set(OrderKey.getMiaoshaOrderByUidGid,
                ""+miaoShaUser.getId()+""+goods.getId(),
                miaoShaOrder);

        return orderInfo;

    }

    public OrderInfo getOrderByOrderId(long orderId) {
        return orderDao.getOrderByOrderId(orderId);
    }
}
