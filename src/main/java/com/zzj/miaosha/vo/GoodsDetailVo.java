package com.zzj.miaosha.vo;


import com.zzj.miaosha.domain.MiaoShaUser;
import lombok.Data;

//用来传输数据的对象，用以演示页面静态化
@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private long remainSeconds = 0;
    private GoodsVo goods;
    private MiaoShaUser user;

}

