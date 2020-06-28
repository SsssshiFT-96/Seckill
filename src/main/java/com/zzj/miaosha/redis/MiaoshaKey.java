package com.zzj.miaosha.redis;

public class MiaoshaKey extends BasePrefix{

    //页面缓存的存在时间非常短，因为页面缓存主要是为了解决短时间内的大量访问
    //如果时间设置太长，则会失去即时性
    private MiaoshaKey(String prefix)
    {

        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
