package com.zzj.miaosha.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
