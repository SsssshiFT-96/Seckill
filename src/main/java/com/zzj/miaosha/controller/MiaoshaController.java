package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.MiaoShaOrder;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.domain.OrderInfo;
import com.zzj.miaosha.rabbitmq.MQSender;
import com.zzj.miaosha.rabbitmq.MiaoshaMessage;
import com.zzj.miaosha.redis.GoodsKey;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.GoodsService;
import com.zzj.miaosha.service.MiaoshaService;
import com.zzj.miaosha.service.OrderService;
import com.zzj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     *
     * 系统初始化
     * 将库存存到redis缓存中
     *
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return;
        }
        //将商品存到缓存中
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);//系统刚开始时标记未结束
        }

    }


    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoShaUser miaoShaUser,
                                     @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", miaoShaUser);
        if(miaoShaUser == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        //1.初始化库存，afterPropertiesSet已做
        //内存标记，用来判断是否秒杀结束，若结束则不用再去访问redis
        Boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //2.将库存减少，返回减少后的库存量
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if(stock < 0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //3.判断是否已经秒杀到，防止一人秒杀多次
        MiaoShaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoShaUser.getId(), goodsId);
        if(miaoshaOrder != null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //4.入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(miaoShaUser);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);//排队中
    }
    /**
     * 下完订单后返回的页面，客户端轮询的页面
     * 返回orderId：成功
     * -1：秒杀失败
     * 0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoShaUser miaoShaUser,
                                   @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", miaoShaUser);
        if(miaoShaUser == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(miaoShaUser.getId(), goodsId);

        return Result.success(result);

    }

    /*
    GET POST有什么区别？
    GET是幂等的
    POST非幂等
     */
    @RequestMapping(value = "/do_miaosha1", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> miaosha1(Model model, MiaoShaUser miaoShaUser,
                       @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", miaoShaUser);
        if(miaoShaUser == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //判断商品库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goods.getStockCount();
        if(stockCount <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到，防止一人秒杀多次
        MiaoShaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoShaUser.getId(), goodsId);
        if(miaoshaOrder != null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //可以秒杀
        //减库存、下订单、写入秒杀订单，这三种应该同时成功同时失败，所以使用事务
        OrderInfo orderInfo = miaoshaService.miaosha(miaoShaUser, goods);
        return Result.success(orderInfo);

    }

    @RequestMapping("/do_miaosha1")
    public String list1(Model model, MiaoShaUser miaoShaUser,
                       @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", miaoShaUser);

        if(miaoShaUser == null){
            return "login";
        }


        //判断商品库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goods.getStockCount();
        if(stockCount <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到，防止一人秒杀多次
        MiaoShaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoShaUser.getId(), goodsId);
        if(miaoshaOrder != null){
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

        //可以秒杀
        //减库存、下订单、写入秒杀订单，这三种应该同时成功同时失败，所以使用事务
        OrderInfo orderInfo = miaoshaService.miaosha(miaoShaUser, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";



    }

}
