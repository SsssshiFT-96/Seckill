package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.GoodsService;
import com.zzj.miaosha.service.MiaoShaUserService;
import com.zzj.miaosha.vo.GoodsVo;
import com.zzj.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String list(Model model, MiaoShaUser miaoShaUser){
        model.addAttribute("user", miaoShaUser);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();

        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MiaoShaUser miaoShaUser,
                         @PathVariable("goodsId")Long goodsId){
        //题外话：一般使用snowflake给商品设置id
        model.addAttribute("user", miaoShaUser);

        //获得秒杀商品详情
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        //秒杀商品状态
        int miaoshaStatus = 0;
        long remainSeconds = 0;
        //获得秒杀商品的开始结束时间
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        if(now < startAt){//秒杀还未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = startAt - now / 1000;
        }else if(now > endAt){//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    }
}
