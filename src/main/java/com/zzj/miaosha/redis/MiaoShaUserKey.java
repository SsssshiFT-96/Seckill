package com.zzj.miaosha.redis;

public class MiaoShaUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 3600*24*2;
    private MiaoShaUserKey(int expireSeconds, String prefix){

        super(expireSeconds, prefix);
    }

    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE,"tkid");
    //对象缓存希望永久有效
    public static MiaoShaUserKey getById = new MiaoShaUserKey(0,"id");
}
