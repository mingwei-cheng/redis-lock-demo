package cn.cheng.lock.server.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author Cheng Mingwei
 * @create 2020-07-16 15:12
 **/
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String url;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;


    @Bean
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+url + ":" + port).setPassword(password);
        return Redisson.create(config);
    }
}
