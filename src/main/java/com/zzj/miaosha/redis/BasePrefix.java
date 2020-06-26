package com.zzj.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix){
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

    //默认0代表永不过期
    public int expireSeconds() {
        return expireSeconds;
    }

    public String getPrefix() {
        //保证不重复
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
