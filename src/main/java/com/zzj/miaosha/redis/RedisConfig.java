package com.zzj.miaosha.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
@Data
//将配置文件中redis打头的读取进来，这里需要引入spring-boot-configuration-processor依赖
public class RedisConfig {
    private String host;
    private Integer port;
    private String password;
    private Integer timeout;
    private Integer poolMaxTotal;
    private Integer poolMaxIdle;
    private Integer poolMaxWait;


}
