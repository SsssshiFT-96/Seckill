package com.zzj.miaosha.service;

import com.zzj.miaosha.domain.Goods;
import com.zzj.miaosha.domain.MiaoShaGoods;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.domain.OrderInfo;
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

    @Transactional
    public OrderInfo miaosha(MiaoShaUser miaoShaUser, GoodsVo goods) {
        //减库存、下订单、写入秒杀订单，这三种应该同时成功同时失败，所以使用事务
        goodsService.reduceStock(goods);

//        System.out.println("减了一次库存");

        return orderService.createOrder(miaoShaUser, goods);


    }
}
