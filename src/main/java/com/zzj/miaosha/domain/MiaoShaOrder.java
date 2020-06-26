package com.zzj.miaosha.domain;

import lombok.Data;

@Data
public class MiaoShaOrder {
    private Long id;
    private Long userId;
    private Long  orderId;
    private Long goodsId;
}
