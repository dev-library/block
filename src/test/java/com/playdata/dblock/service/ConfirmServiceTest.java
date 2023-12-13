package com.playdata.dblock.service;

import com.playdata.dblock.repository.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ConfirmServiceTest {

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
    @DisplayName("쿠폰을 하나 발급해서 디비에 하나의 내역이 저장되는지 확인한다.")
    public void 쿠폰하나만발급하기(){
        confirmService.confirm(1L);

        assertEquals(1, couponRepository.count());
    }

    @Test
    @DisplayName("쿠폰발급은 멀티쓰레드 형식으로 1000개의 요청을 넣고, 100개까지만 발급되는지 확인")
    public void 쿠폰1000개요청100개만만들기() throws InterruptedException {
        int threadCount = 1000; //요청 횟수는 1000개
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for(int i = 0; i < threadCount; i++){
            long userId = i;
            executorService.submit(() -> {
               try {
                   confirmService.confirm(userId);
               } finally {
                   countDownLatch.countDown();
               }
            });
        }
        countDownLatch.await();

        assertEquals(100, couponRepository.count());
    }
}
