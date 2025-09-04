package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.CourierGeolocation;
import com.cok.couriertracking.dto.CourierGeolocationResponse;
import com.cok.couriertracking.dto.CreateCourierGeolocationRequest;
import com.cok.couriertracking.exception.GeolocationRetryFailedException;
import com.cok.couriertracking.repository.CourierGeolocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourierGeolocationService {

    private final CourierGeolocationRepository courierGeolocationRepository;
    private final CourierTrackingService courierTrackingService;

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
        List<CourierGeolocation> logs = courierGeolocationRepository.findByCourierIdOrderByTimestampAsc(courierId);
        return logs.isEmpty() ? 0.0 : logs.getLast().getTotalDistance();
    }

}
