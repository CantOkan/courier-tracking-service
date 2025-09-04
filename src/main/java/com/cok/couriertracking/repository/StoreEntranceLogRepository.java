package com.cok.couriertracking.repository;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.domain.StoreEntranceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreEntranceLogRepository extends JpaRepository<StoreEntranceLog, Long> {
    Optional<StoreEntranceLog> findTopByCourierAndStoreIdOrderByTimestampDesc(Courier courier, Long storeId);
}
