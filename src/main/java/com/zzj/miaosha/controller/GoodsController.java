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
    public String toList(Model model, MiaoShaUser miaoShaUser){
        model.addAttribute("miaoShaUser", miaoShaUser);
        return "goods_list";
    }
}
