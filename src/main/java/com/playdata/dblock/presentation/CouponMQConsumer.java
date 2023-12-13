package com.playdata.dblock.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playdata.dblock.entity.Coupon;
import com.playdata.dblock.service.ConfirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponMQConsumer {

    private final ObjectMapper objectMapper;

    private final ConfirmService confirmService;

    @RabbitListener(queues = "${queuename}")
    public void createCoupon(String message) throws JsonProcessingException {
        System.out.println("컨슈머 : " + message + "를 컨슘햇습니다!");
        // 메세지큐에서 컨슘하면 무조건 String 자료형이 받아짐
        // 따라서 해당 자료를 역직렬화 해서 자바에서 쓸 수 있는 자료로 환원해야함.
        Coupon coupon = objectMapper.readValue(message, Coupon.class);
        // userId를 기반으로 쿠폰을 생성하기 때문에 예외적으로 userId만 얻어옴.
        confirmService.confirm(coupon.getUserId());

    }




}
