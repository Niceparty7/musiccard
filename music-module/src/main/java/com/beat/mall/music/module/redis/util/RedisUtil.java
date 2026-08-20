package com.beat.mall.music.module.redis.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Redis 操作封装：String / List 类型 + 前缀删除
 * 所有方法内部捕获异常并降级返回空值，Redis 不可用时业务自动回源数据库，不影响服务可用性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final JedisPool jedisPool;

    // ===================== String 类型 =====================

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            log.error("Redis get error, key:{}", key, e);
            return null;
        }
    }

    /**
     * 写入带过期时间：seconds 秒后自动删除
     */
    public void setex(String key, int seconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        } catch (Exception e) {
            log.error("Redis setex error, key:{}", key, e);
        }
    }

    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            log.error("Redis del error, key:{}", key, e);
        }
    }

    public boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("Redis exists error, key:{}", key, e);
            return false;
        }
    }

    /**
     * 剩余过期秒数；-2 表示 key 不存在，-1 表示无过期时间
     */
    public long ttl(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(key);
        } catch (Exception e) {
            log.error("Redis ttl error, key:{}", key, e);
            return -2;
        }
    }

    public void expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("Redis expire error, key:{}", key, e);
        }
    }

    /**
     * 删除指定前缀的所有 key。keys 为 O(N) 命令，当前数据量小可接受，数据增长后建议换 SCAN
     */
    public void delByPrefix(String prefix) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
                log.info("Redis delByPrefix, prefix:{}, count:{}", prefix, keys.size());
            }
        } catch (Exception e) {
            log.error("Redis delByPrefix error, prefix:{}", prefix, e);
        }
    }

    // ===================== List 类型 =====================

    /**
     * 尾部追加，返回当前长度
     */
    public long rpush(String key, String... values) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpush(key, values);
        } catch (Exception e) {
            log.error("Redis rpush error, key:{}", key, e);
            return 0;
        }
    }

    /**
     * 按索引切片读取，start/end 含端点；end 为 -1 表示取到队尾
     */
    public List<String> lrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("Redis lrange error, key:{}", key, e);
            return Collections.emptyList();
        }
    }

    public long llen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("Redis llen error, key:{}", key, e);
            return 0;
        }
    }
}
