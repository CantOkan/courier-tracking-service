package com.cok.couriertracking.observer;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.domain.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CourierNearStoreEvent {
    private Courier courier;
    private Store store;
    private final LocalDateTime timestamp;
}
