package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.MiaoShaOrder;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.domain.OrderInfo;
import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.service.GoodsService;
import com.zzj.miaosha.service.MiaoshaService;
import com.zzj.miaosha.service.OrderService;
import com.zzj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoShaUser miaoShaUser,
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