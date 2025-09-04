package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.domain.CourierGeolocation;
import com.cok.couriertracking.domain.Store;
import com.cok.couriertracking.dto.CourierGeolocationResponse;
import com.cok.couriertracking.dto.CreateCourierGeolocationRequest;
import com.cok.couriertracking.observer.CourierNearStoreEvent;
import com.cok.couriertracking.observer.CourierStoreObserver;
import com.cok.couriertracking.repository.CourierGeolocationRepository;
import com.cok.couriertracking.strategy.DistanceCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierTrackingService {

    private final CourierGeolocationRepository courierGeolocationRepository;
    private final CourierService courierService;
    private final DistanceCalculationStrategy distanceCalculator;
    private final StoreLoader storeLoader;
    private final List<CourierStoreObserver> observers;

    private static final double STORE_RADIUS_KM = 0.1; // 100 meters = 0.1 km

    @Transactional
    protected CourierGeolocationResponse persistLocation(CreateCourierGeolocationRequest request) {
        Courier courier = courierService.findById(request.courierId());

        double totalDistance = 0.0;

        List<CourierGeolocation> logs = courierGeolocationRepository.findByCourierIdOrderByTimestampAsc(courier.getId());

        if (!logs.isEmpty()) {
            CourierGeolocation lastLog = logs.getLast();
            double distance = distanceCalculator.calculateDistance(
                    lastLog.getLat(), lastLog.getLng(),
                    request.lat(), request.lng()
            );
            totalDistance = lastLog.getTotalDistance() + distance;
        }

        CourierGeolocation newLog = CourierGeolocation.builder()
                .courier(courier)
                .lat(request.lat())
                .lng(request.lng())
                .timestamp(LocalDateTime.now())
                .totalDistance(totalDistance)
                .build();

        CourierGeolocation saved = courierGeolocationRepository.save(newLog);
        checkDistanceAndPublishEvent(courier, saved);
        return new CourierGeolocationResponse(courier.getId(), saved.getLat(), saved.getLng(), saved.getTimestamp(), saved.getTotalDistance());
    }

    public void checkDistanceAndPublishEvent(Courier courier, CourierGeolocation location) {
        for (Store store : storeLoader.getStores()) {
            double distance = distanceCalculator.calculateDistance(
                    location.getLat(), location.getLng(),
                    store.getLat(), store.getLng()
            );
            if (distance <= STORE_RADIUS_KM) {
                CourierNearStoreEvent event = CourierNearStoreEvent.builder().courier(courier).store(store).timestamp(LocalDateTime.now()).build();
                observers.forEach(observer -> observer.handle(event));
            }
        }
    }

}
