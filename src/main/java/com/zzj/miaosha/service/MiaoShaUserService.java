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
        //1.取缓存
        MiaoShaUser user = redisService.get(MiaoShaUserKey.getById, "" + id, MiaoShaUser.class);
        if(user != null){//若缓存中有则返回
            return user;
        }
        //2.缓存中没有就手动从数据库中取出
        user = miaoShaUserDao.getById(id);
        //3.存入缓存
        if(user != null){
            redisService.set(MiaoShaUserKey.getById,"" + id, user);
        }

//        MiaoShaUser miaoShaUser = miaoShaUserDao.getById(id);
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass){
        //取User
        MiaoShaUser user = getById(id);
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoShaUser toBeUpdate = new MiaoShaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassDBPass(formPass, user.getSalt()));
        miaoShaUserDao.update(toBeUpdate);
        //处理缓存，跟user有关的缓存都需要改掉
        //删掉旧对象
        redisService.delete(MiaoShaUserKey.getById, ""+id);
        //token开头的key不能删除，因为还要登录,所以是更新
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoShaUserKey.token, token, user);

        return true;
    }


    public String login(HttpServletResponse response, LoginVo loginVo){

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
        String token = UUIDUtil.uuid();
        addCookie(response, token, miaoShaUser);
        return token;
    }

    public MiaoShaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }

        MiaoShaUser miaoShaUser = redisService.get(MiaoShaUserKey.token, token, MiaoShaUser.class);
        //延长有效期
        if(miaoShaUser != null){
            addCookie(response, token, miaoShaUser);
        }
        return miaoShaUser;
    }

    public void addCookie(HttpServletResponse response, String token, MiaoShaUser miaoShaUser){
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
