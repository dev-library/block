package com.playdata.dblock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playdata.dblock.entity.Coupon;
import com.playdata.dblock.repository.CouponCountRepository;
import com.playdata.dblock.repository.CouponMQProducer;
import com.playdata.dblock.repository.CouponRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final CouponRepository couponRepository;

    private final CouponCountRepository couponCountRepository;

    private final CouponMQProducer couponMQProducer;

    private final ObjectMapper objectMapper;

    public void confirm(Long userId){
        // DB에 직접 질의해서 요청 횟수 카운트하기
        long count = couponRepository.count(); // JPA를 이용해 해당 테이블(Coupon) row 개수 체크

        // 레디스에서 아토믹연산화 시켜서 하는 카운트
        //Long count = couponCountRepository.increment(); // 1증가 시킨 후, 증가된 해당 값을 리턴함.

        if(count >= 100){ // 레디스를 활용하지 않는 경우
        //if(count > 100){ // 레디스 활용하는 경우
            return; // 발급된 쿠폰이 100개가 넘으면 발급 방지
        }

        couponRepository.save(new Coupon(userId));
    }

    // 메세지큐에 적재하는 로직
    public void sendMessage(Long userId) throws JsonProcessingException {
        // 받아온 userId를 이용해 Coupon객체 생성
        Coupon coupon = new Coupon(userId);

        // 생성된 객체를 ObjectMapper를 이용해 json으로 직렬화
        String message = objectMapper.writeValueAsString(coupon);

        // 직렬화 후에 메세지큐로 전송
        couponMQProducer.sendCreateCouponMessage(message);
    }

    public void reset(){
        couponCountRepository.reset();
    }

}






