package com.playdata.dblock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LettuceRepository {

    // 레디스 요소는 키도 String, 벨류도 String
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key){
        // 레디스 내부에 키값이 존재하지 않으면 새로 만들어주는 명령어
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    }

    public Boolean unlock(Long key){
        // 특정 키값을 지우기
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(Long key){
        return key.toString();
    }
}
