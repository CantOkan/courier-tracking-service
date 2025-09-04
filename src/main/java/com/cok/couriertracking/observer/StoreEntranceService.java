package com.cok.couriertracking.observer;

import com.cok.couriertracking.domain.StoreEntranceLog;
import com.cok.couriertracking.repository.StoreEntranceLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreEntranceService implements CourierStoreObserver {

    private final StoreEntranceLogRepository storeEntranceLogRepository;

    private static final int REENTRY_LIMIT_MINS = 1;

    @Async
    @Override
    public void handle(CourierNearStoreEvent courierNearStoreEvent) {
        Optional<StoreEntranceLog> lastLog = storeEntranceLogRepository.findTopByCourierAndStoreIdOrderByTimestampDesc(courierNearStoreEvent.getCourier(), courierNearStoreEvent.getStore().getId());
        if (lastLog.isPresent() && Duration.between(lastLog.get().getTimestamp(), LocalDateTime.now()).toMinutes() < REENTRY_LIMIT_MINS) {
            return; // skip re-entry
        }

        StoreEntranceLog storeEntranceLog = new StoreEntranceLog();
        storeEntranceLog.setCourier(courierNearStoreEvent.getCourier());
        storeEntranceLog.setStoreId(courierNearStoreEvent.getStore().getId());
        storeEntranceLog.setTimestamp(courierNearStoreEvent.getTimestamp());
        storeEntranceLogRepository.save(storeEntranceLog);

        log.info("Courier {} entered store '{}' (ID: {}) at {}",
                courierNearStoreEvent.getCourier().getId(), courierNearStoreEvent.getStore().getName(), courierNearStoreEvent.getStore().getId(), courierNearStoreEvent.getTimestamp());
    }

}
