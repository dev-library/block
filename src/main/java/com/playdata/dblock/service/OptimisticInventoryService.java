package com.playdata.dblock.service;

import com.playdata.dblock.entity.Inventory;
import com.playdata.dblock.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OptimisticInventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void decrease(Long id, Long count) { // 아이템번호와 감소시킬 수량을 적으면

            Inventory inventory = inventoryRepository.findByIdOptimistic(id);// 낙관적 락을 활용해 조회

            inventory.decrease(count); // 감소시키고

            inventoryRepository.saveAndFlush(inventory); // 디비에 반영하고 락 해제

    }
}
