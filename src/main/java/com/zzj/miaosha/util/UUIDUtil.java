package com.zzj.miaosha.util;

import java.util.UUID;

public class UUIDUtil {
    public static String uuid(){
        //后面replace是将UUID中的横杠去掉
        return UUID.randomUUID().toString().replace("-","");
    }
}
