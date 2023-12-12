package com.playdata.dblock.facade;

import com.playdata.dblock.repository.LettuceRepository;
import com.playdata.dblock.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LettuceLockFacade {

    private final LettuceRepository lettuceRepository; // 락을 걸어주는 레포지토리 빈

    private final InventoryService inventoryService; // 락이 걸리지 않은 기능구현 빈

    public void decrease(Long key, Long count) throws InterruptedException{
        // InventoryService의 decrease연산 앞 뒤에 락을 걸고 해제하는 코드를 미리 추가함.
        while(!lettuceRepository.lock(key)){
            Thread.sleep(100);
        }// 락 점유 시도, 성공시 false, 실패시 true가 걸리므로 재점유를 시도하도록 반복문 설계

        try{
            inventoryService.decrease(key, count);
        } finally {
            lettuceRepository.unlock(key);
        }// 예외없이 성공적으로 구매에 따른 재고 감소가 이뤄졌다면 락 풀기


    }

}





