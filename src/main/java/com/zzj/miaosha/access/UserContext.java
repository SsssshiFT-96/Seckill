package com.zzj.miaosha.access;

import com.zzj.miaosha.domain.MiaoShaUser;
import lombok.Data;

//用来接收存储页面上的用户信息
public class UserContext {
    /*
    ThreadLocal当前线程绑定，往ThreadLocal放东西是放在当前线程中
     */
    private static ThreadLocal<MiaoShaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoShaUser user){
        userHolder.set(user);
    }

    public static MiaoShaUser getUser(){
        return userHolder.get();
    }

}
