package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierGeolocationResponse;
import com.cok.couriertracking.dto.CreateCourierGeolocationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierGeolocationServiceTest {

    private static final Long COURIER_ID = 1L;
    private static final Double LAT = 41.0082;
    private static final Double LNG = 28.9784;

    @Mock
    private CourierTrackingService courierTrackingService;

    @Mock
    private CourierService courierService;

    @InjectMocks
    private CourierGeolocationService courierGeolocationService;

    @Test
    void Should_CreateLocationAndReturnResponse_When_ValidRequestProvided() {

        CreateCourierGeolocationRequest request = generateCreateLocationRequest();
        CourierGeolocationResponse expectedResponse = generateLocationResponse();

        when(courierTrackingService.persistLocation(request)).thenReturn(expectedResponse);

        CourierGeolocationResponse result = courierGeolocationService.createLocation(request);

        assertThat(result).isNotNull();
        assertEquals(COURIER_ID, result.courierId());
        assertEquals(LAT, result.lat());
        assertEquals(LNG, result.lng());

        verify(courierTrackingService, times(1)).persistLocation(request);
    }

    @Test
    void Should_ThrowRuntimeException_When_OptimisticLockFailureExceedsMaxRetries() {
        CreateCourierGeolocationRequest request = generateCreateLocationRequest();

        when(courierTrackingService.persistLocation(request)).thenThrow(new ObjectOptimisticLockingFailureException("Lock failure", new RuntimeException()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courierGeolocationService.createLocation(request));

        assertEquals(String.format("Failed to save courier location after 4 retries for courier %d", COURIER_ID), exception.getMessage());
        assertInstanceOf(ObjectOptimisticLockingFailureException.class, exception.getCause());
        verify(courierTrackingService, times(4)).persistLocation(request); // 1 initial + 3 retries
    }

    @Test
    void Should_ReturnTotalDistanceFromLastLog_When_CourierGeolocationLogsExist() {
        Long courierId = 1L;
        double expectedTotalDistance = 1500.0;
        Courier courier = generateCourier();
        courier.setTotalDistance(expectedTotalDistance);

        when(courierService.findById(courierId)).thenReturn(courier);
        double result = courierGeolocationService.getTotalTravelDistance(courierId);

        assertEquals(expectedTotalDistance, result);
        verify(courierService, times(1)).findById(courierId);
    }

    private CreateCourierGeolocationRequest generateCreateLocationRequest() {
        return new CreateCourierGeolocationRequest(COURIER_ID, LAT, LNG);
    }

    private CourierGeolocationResponse generateLocationResponse() {
        return new CourierGeolocationResponse(COURIER_ID, LAT, LNG, LocalDateTime.now());
    }

    private Courier generateCourier() {
        Courier courier = new Courier();
        courier.setId(COURIER_ID);
        courier.setFullName("Can Okan");
        courier.setLicensePlate("34ABC123");
        return courier;
    }


}