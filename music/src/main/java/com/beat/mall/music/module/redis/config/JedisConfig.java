package com.beat.mall.music.module.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis 连接池配置，绑定 application.properties 中 redis.* 前缀
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class JedisConfig {

    private String host = "localhost";
    private int port = 6379;
    private String password = "";
    private int database = 0;
    private int timeout = 3000;
    private Pool pool = new Pool();

    @Data
    public static class Pool {
        private int maxTotal = 8;
        private int maxIdle = 8;
        private int minIdle = 0;
        private long maxWait = 3000;
    }

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(pool.getMaxTotal());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait());
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setJmxEnabled(false);

        // password 为空时传 null，避免误带密码握手
        String pwd = (password == null || password.isEmpty()) ? null : password;
        return new JedisPool(poolConfig, host, port, timeout, pwd, database);
    }
}
