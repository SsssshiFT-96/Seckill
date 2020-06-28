package com.zzj.miaosha.service;

import com.zzj.miaosha.domain.*;
import com.zzj.miaosha.redis.MiaoshaKey;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    //在service中不提倡引入别的dao，但能引用别的service
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoShaUser miaoShaUser, GoodsVo goods) {
        //减库存、下订单、写入秒杀订单，这三种应该同时成功同时失败，所以使用事务
        boolean success = goodsService.reduceStock(goods);

//        System.out.println("减了一次库存");
        if(success){
            return orderService.createOrder(miaoShaUser, goods);
        }else{//没有库存意味着秒杀失败，然后这里在缓存做一个标记，等客户端轮询时会用到
            setGoodsOver(goods.getId());
            return null;
        }

    }


    //返回秒杀结果
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(order != null){//秒杀成功
            return  order.getOrderId();
        }else{//不成功判断是否是库存为0
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }

    }


    private void setGoodsOver(Long goodsId) {

        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        //判断缓存中是否存在isGoodsOver的key，存在就是买完了。
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }
}
