package com.cok.couriertracking.observer;

public interface CourierStoreObserver {
    void handle(CourierNearStoreEvent courierNearStoreEvent);
}
