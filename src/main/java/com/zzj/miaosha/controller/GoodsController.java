package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.redis.GoodsKey;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.GoodsService;
import com.zzj.miaosha.service.MiaoShaUserService;
import com.zzj.miaosha.vo.GoodsDetailVo;
import com.zzj.miaosha.vo.GoodsVo;
import com.zzj.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    RedisService redisService;

    //使用框架提供的ThymeleafViewResolver来进行渲染
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody//直接返回HTML源代码
    public String list(Model model, MiaoShaUser miaoShaUser,
                       HttpServletResponse response,
                       HttpServletRequest request){
        model.addAttribute("user", miaoShaUser);
        //直接返回html源代码
        //1.先从缓存中看能否取到
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){//如果不是空就返回
            return html;
        }

        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();

        model.addAttribute("goodsList", goodsList);

        //2.如果缓存中没有，就手工渲染。方法参数为：模板名称，模板需要的参数如request等
        //因为项目使用的是thymeleaf.spring5的版本把SpringWebContext大部分的功能移到了IWebContext下面
        //用来区分边界。剔除了ApplicationContext 过多的依赖，现在thymeleaf渲染不再过多依赖spring容器
        IWebContext ctx = new WebContext(request, response, request.getServletContext(),
                                        request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        //3.若html非空，则将其存到缓存中
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"", html);
        }

        return html;
//        return "goods_list";
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(Model model, MiaoShaUser miaoShaUser,
                                        @PathVariable("goodsId")Long goodsId,
                                        HttpServletResponse response,
                                        HttpServletRequest request){
        //获得秒杀商品详情
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        //秒杀商品状态
        int miaoshaStatus = 0;
        long remainSeconds = 0;
        //获得秒杀商品的开始结束时间
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        if(now < startAt){//秒杀还未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (startAt - now) / 1000;
        }else if(now > endAt){//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(miaoShaUser);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);
    }

    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail2(Model model, MiaoShaUser miaoShaUser,
                         @PathVariable("goodsId")Long goodsId,
                         HttpServletResponse response,
                         HttpServletRequest request){
        //题外话：一般使用snowflake给商品设置id
        model.addAttribute("user", miaoShaUser);

        //1.先从缓存中看能否取到
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if(!StringUtils.isEmpty(html)){//如果不是空就返回
            return html;
        }

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
//        return "goods_detail";
        //2.如果缓存中没有，就手工渲染。
        IWebContext ctx = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        //3.若html非空，则将其存到缓存中
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,"" + goodsId, html);
        }
        return html;
    }
}
