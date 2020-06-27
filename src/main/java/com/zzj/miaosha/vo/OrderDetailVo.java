package com.zzj.miaosha.vo;

import com.zzj.miaosha.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
    GoodsVo goods;
    OrderInfo order;

}
