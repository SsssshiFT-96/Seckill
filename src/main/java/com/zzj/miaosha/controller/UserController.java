package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//尝试单纯获取对象的QPS能有多少
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoShaUser> info(Model model, MiaoShaUser user){
        return  Result.success(user);

    }
}
