package com.playdata.dblock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponCountRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Long increment() {                       // incr couponcount
        return redisTemplate.opsForValue().increment("couponcount");
    }

    public void reset() {               // set couponcount 0
        redisTemplate.opsForValue().set("couponcount", "0");
    }
}
