package com.zzj.miaosha.vo;

import com.zzj.miaosha.domain.Goods;
import lombok.Data;

import java.util.Date;

//将物品表和秒杀物品表属性集合，获得真正的秒杀的物品属性
@Data
public class GoodsVo extends Goods {
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
