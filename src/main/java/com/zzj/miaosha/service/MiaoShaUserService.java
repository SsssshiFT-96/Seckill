package com.zzj.miaosha.service;

import com.zzj.miaosha.dao.MiaoShaUserDao;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.exception.GlobalException;
import com.zzj.miaosha.redis.MiaoShaUserKey;
import com.zzj.miaosha.redis.RedisService;
import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.util.MD5Util;
import com.zzj.miaosha.util.UUIDUtil;
import com.zzj.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {

    public
    static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoShaUser getById(Long id){

        MiaoShaUser miaoShaUser = miaoShaUserDao.getById(id);
        return miaoShaUser;
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo){

        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断手机号是否存在
        MiaoShaUser miaoShaUser = getById(Long.parseLong(mobile));
        if(miaoShaUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoShaUser.getPassword();
        String dbSalt = miaoShaUser.getSalt();
        String calcPass = MD5Util.formPassDBPass(password, dbSalt);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成cookie
        addCookie(response, miaoShaUser);
        return true;
    }

    public MiaoShaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }

        MiaoShaUser miaoShaUser = redisService.get(MiaoShaUserKey.token, token, MiaoShaUser.class);
        //延长有效期
        if(miaoShaUser != null){
            addCookie(response, miaoShaUser);
        }
        return miaoShaUser;
    }

    public void addCookie(HttpServletResponse response, MiaoShaUser miaoShaUser){
        String token = UUIDUtil.uuid();
        //将第三方的信息存到redis缓存中
        redisService.set(MiaoShaUserKey.token, token, miaoShaUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置cookie有效期
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        //将cookie写到客户端中
        response.addCookie(cookie);
    }
}
