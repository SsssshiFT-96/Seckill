package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.MiaoShaUserService;
import com.zzj.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @RequestMapping("/to_list")
    public String toList(HttpServletResponse response, Model model,
                         //获取客户端上的token
                         @CookieValue(value = MiaoShaUserService.COOKIE_NAME_TOKEN,
                         required = false) String cookieToken,
                         //很多手机端不会把token放在cookie中，而是直接放到参数中传
                         @RequestParam(value=MiaoShaUserService.COOKIE_NAME_TOKEN,
                         required = false) String paramToken){
        //如果取到的token为空，则返回登录页面登录
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        //优先取paramToken
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;

        MiaoShaUser miaoShaUser = miaoShaUserService.getByToken(response, token);

        model.addAttribute("miaoShaUser", miaoShaUser);
        return "goods_list";
    }
}
