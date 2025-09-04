package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierGeolocationResponse;
import com.cok.couriertracking.dto.CreateCourierGeolocationRequest;
import com.cok.couriertracking.exception.GeolocationRetryFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourierGeolocationService {

    private final CourierTrackingService courierTrackingService;
    private final CourierService courierService;


    public CourierGeolocationResponse createLocation(CreateCourierGeolocationRequest request) {
        int retries = 0;
        int maxRetries = 3;
        while (true) {
            try {
                return courierTrackingService.persistLocation(request);
            } catch (ObjectOptimisticLockingFailureException e) {
                ++retries;
                if (retries > maxRetries) {
                    throw new GeolocationRetryFailedException(request.courierId(), retries, e);
                }
                log.warn("Optimistic lock failure, retrying {}/{}", retries, maxRetries);
            }
        }
    }

    @Transactional(readOnly = true)
    public double getTotalTravelDistance(Long courierId) {
        Courier courier = courierService.findById(courierId);
        return courier.getTotalDistance();
    }
}
