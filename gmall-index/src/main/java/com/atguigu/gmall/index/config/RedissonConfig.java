package com.atguigu.gmall.index.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 凯少
 * @create 2019-12-13 18:10
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient getConfig(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.199.199:6379");
        RedissonClient redisson = Redisson.create(config);
        return  redisson;
    }
}
