package com.playdata.dblock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.playdata.dblock.repository.CouponRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CouponMQTest {

    @Autowired
    private ConfirmService confirmService;

    @Autowired
    private CouponRepository couponRepository;

    @AfterEach
    public void dbreset(){
        couponRepository.deleteAll(); // 모든 로직이 끝나면 디비 초기화
        confirmService.reset();
    }

    @Test
    public void CouponInsertWithMQ() throws JsonProcessingException, InterruptedException {
        int threadCount = 1000; //요청 횟수는 1000개
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for(int i = 0; i < threadCount; i++){
            long userId = i;
            executorService.submit(() -> {
                try {
                    // 현재 테스트코드에서 저장해주는 로직은 존재하지 않습니다.
                    // 메세지큐에 동시다발적으로 쿠폰발급 요청을 할 뿐입니다.
                    confirmService.sendMessage(userId);
                } catch(Exception e) {}
                finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // 10초 기다리기.
        Thread.sleep(10000);

        assertEquals(100, couponRepository.count());
    }
}

