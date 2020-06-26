package com.zzj.miaosha.domain;

import lombok.Data;

import java.util.Date;

@Data
public class MiaoShaGoods {
    private Long id;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
