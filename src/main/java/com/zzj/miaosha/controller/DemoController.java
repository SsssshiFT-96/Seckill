package com.zzj.miaosha.controller;

import com.zzj.miaosha.domain.User;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.redis.UserKey;
import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.result.Result;
import com.zzj.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {


    //controller返回分为两大类：1.rest api json输出 2.页面，用到thymeleaf
    //使用json输出
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){
        return Result.success("hello test");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<CodeMsg> helloError(){
         return Result.error(CodeMsg.SERVER_ERROR);
    }

    //使用页面输出
    @RequestMapping("/helloThymeleaf")
    public String helloError(Model model){
        model.addAttribute("name", "Thymeleaf");
        return "hello";
    }

    @Autowired
    UserService userService;

    //测试Mybatis
    @RequestMapping("/dbTest")
    @ResponseBody
    public Result<User> dbTest(){
        User user = userService.getUserById(1L);
        return Result.success(user);
    }

    @Autowired
    RedisService redisService;

    //测试Redis
    @RequestMapping("/redisTest")
    @ResponseBody
    public Result<User> redisTest(){
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    //测试Redis
    @RequestMapping("/redisTest2")
    @ResponseBody
    public Result<Boolean> redisTest2(){
        User user = new User();
        user.setId(1L);
        user.setName("11111");
        boolean res = redisService.set(UserKey.getById,"" + 1, user);
        return Result.success(res);
    }

}
