package com.zzj.miaosha.rabbitmq;

import com.zzj.miaosha.domain.MiaoShaUser;
import lombok.Data;

@Data
public class MiaoshaMessage {
    private MiaoShaUser user;
    private long goodsId;

}
