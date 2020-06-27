package com.zzj.miaosha.controller;

import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.MiaoShaUserService;
import com.zzj.miaosha.util.ValidatorUtil;
import com.zzj.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response,
                                   @Valid LoginVo loginVo){

        //登录
        String token = miaoShaUserService.login(response, loginVo);
        return Result.success(token);

    }

}
