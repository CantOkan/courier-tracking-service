package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourierTrackingService {

    private final StoreLoader storeLoader;

    public CourierTrackingService(StoreLoader storeLoader) {
        this.storeLoader = storeLoader;
    }

    public void checkStores() {
        for (Store store : storeLoader.getStores()) {
            log.info("Loaded Store: " + store.getName() +
                    " (" + store.getLat() + ", " + store.getLng() + ")");
        }
    }

}
