package com.zzj.miaosha.redis;

public class GoodsKey extends BasePrefix{

    //页面缓存的存在时间非常短，因为页面缓存主要是为了解决短时间内的大量访问
    //如果时间设置太长，则会失去即时性
    private GoodsKey(int expireSeconds, String prefix)
    {

        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"gs");
}
