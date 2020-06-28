package com.zzj.miaosha.redis;

public class MiaoshaKey extends BasePrefix{

    //页面缓存的存在时间非常短，因为页面缓存主要是为了解决短时间内的大量访问
    //如果时间设置太长，则会失去即时性
    private MiaoshaKey(int expireSeconds, String prefix)
    {

        super(expireSeconds, prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300,"vc");
}
