package com.playdata.dblock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
@Component
@RequiredArgsConstructor
public class CouponMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendCreateCouponMessage(String message) {
        System.out.println("프로듀서 : " + message + "를 입력받아 큐에 적재합니다.");
        rabbitTemplate.convertAndSend("COUPON_ATOMIC_QUEUE", message);
    }
}
