package com.zzj.miaosha.rabbitmq;

import com.zzj.miaosha.domain.MiaoShaOrder;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.domain.OrderInfo;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.GoodsService;
import com.zzj.miaosha.service.MiaoshaService;
import com.zzj.miaosha.service.OrderService;
import com.zzj.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        log.info("receive message" + message);
        MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoShaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();
        //开始异步处理
        //判断库存，这里可以访问数据库，因为进队列的就很少了
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goods.getStockCount();
        if(stockCount <= 0){
            return;
        }
        //再次判断是否秒杀到
        MiaoShaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(miaoshaOrder != null){
            return;
        }
        //可以秒杀，生成秒杀订单
        //减库存、下订单、写入秒杀订单，这三种应该同时成功同时失败，所以使用事务
        miaoshaService.miaosha(user, goods);

    }



    /*@RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){

        log.info("receive message" + message);

    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){

        log.info("tp1 receive message" + message);

    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){

        log.info("tp2 receive message" + message);

    }*/


}
