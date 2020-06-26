package com.zzj.miaosha.service;

import com.zzj.miaosha.dao.MiaoShaUserDao;
import com.zzj.miaosha.domain.MiaoShaUser;
import com.zzj.miaosha.exception.GlobalException;
import com.zzj.miaosha.result.CodeMsg;
import com.zzj.miaosha.util.MD5Util;
import com.zzj.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoShaUserService {

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    public MiaoShaUser getById(Long id){

        MiaoShaUser miaoShaUser = miaoShaUserDao.getById(id);
        return miaoShaUser;
    }

    public boolean login(LoginVo loginVo){

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
        return true;

    }
}
