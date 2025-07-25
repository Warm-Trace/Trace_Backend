package com.example.trace.auth.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void save(String key, Object val, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, val, time, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    public Long incrementWithTTL(String key, long timeout, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            log.error("Redis increment operation returned null for key: {}", key);
            throw new RuntimeException("Redis increment operation failed");
        } else if (count == 1) {
            // 첫 번째 증가일 때만 TTL 설정
            redisTemplate.expire(key, timeout, timeUnit);
        }
        return count;
    }

    public String getDailyVerificationKey(String providerId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("verification:daily:%s:%s", providerId, today);
    }
}
