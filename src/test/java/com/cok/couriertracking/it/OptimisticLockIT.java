package com.cok.couriertracking.it;

import com.cok.couriertracking.config.PostgresContainerConfig;
import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierRequest;
import com.cok.couriertracking.dto.CreateCourierGeolocationRequest;
import com.cok.couriertracking.exception.GeolocationRetryFailedException;
import com.cok.couriertracking.service.CourierGeolocationService;
import com.cok.couriertracking.service.CourierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OptimisticLockIT extends PostgresContainerConfig {

    @Autowired
    CourierService courierService;

    @Autowired
    CourierGeolocationService courierGeolocationService;

    Courier courier;

    @BeforeEach
    void setup() {
        courier = courierService.create(new CourierRequest("Courier", "34CAN01"));
        courierGeolocationService.createLocation(
                new CreateCourierGeolocationRequest(courier.getId(), 40.99, 28.99));
    }

    @Test
    void shouldHandleOptimisticLockConflict() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CreateCourierGeolocationRequest request1 = new CreateCourierGeolocationRequest(courier.getId(), 41.00, 28.00);
        CreateCourierGeolocationRequest request2 = new CreateCourierGeolocationRequest(courier.getId(), 42.00, 29.00);

        Callable<Void> task1 = () -> { courierGeolocationService.createLocation(request1); return null; };
        Callable<Void> task2 = () -> { courierGeolocationService.createLocation(request2); return null; };

        Future<Void> f1 = executor.submit(task1);
        Future<Void> f2 = executor.submit(task2);

        try {
            f1.get();
            f2.get();
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(GeolocationRetryFailedException.class);
        }

        double distance = courierGeolocationService.getTotalTravelDistance(courier.getId());
        assertThat(distance).isGreaterThan(0.0);

        Courier updated = courierService.findById(courier.getId());
        assertThat(updated.getVersion()).isGreaterThan(1L);
    }
}
