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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierTrackingServiceTest {

    private static final Long COURIER_ID = 1L;
    private static final Double LAT = 41.0082;
    private static final Double LNG = 28.9784;
    private static final Double DISTANCE_WITHIN_RADIUS = 0.01;
    private static final Double DISTANCE_OUTSIDE_RADIUS_RADIUS = 0.2;

    @Mock
    private CourierGeolocationRepository courierGeolocationRepository;

    @Mock
    private CourierService courierService;

    @Mock
    private DistanceCalculationStrategy distanceCalculator;

    @Mock
    private StoreLoader storeLoader;

    @Mock
    private CourierStoreObserver observer1;

    @InjectMocks
    private CourierTrackingService courierTrackingService;

    @Test
    void Should_PersistLocationAndReturnResponse_When_FirstLocationForCourier() {
        CreateCourierGeolocationRequest request = generateCourierGeolocationRequest();
        Courier courier = generateCourier();
        CourierGeolocation savedLocation = generateCourierGeolocation(0.0, LAT, LNG);

        when(courierService.findById(COURIER_ID)).thenReturn(courier);
        when(courierGeolocationRepository.findByCourierIdOrderByTimestampAsc(COURIER_ID)).thenReturn(Collections.emptyList());
        when(courierGeolocationRepository.save(any(CourierGeolocation.class))).thenReturn(savedLocation);

        CourierGeolocationResponse result = courierTrackingService.persistLocation(request);

        assertThat(result).isNotNull();
        assertThat(result.courierId()).isEqualTo(COURIER_ID);
        assertEquals(LAT, result.lat());
        assertEquals(LNG, result.lng());

        assertThat(result.totalDistance()).isEqualTo(0.0);

        verify(courierService, times(1)).findById(COURIER_ID);
        verify(courierGeolocationRepository, times(1)).findByCourierIdOrderByTimestampAsc(COURIER_ID);
        verify(courierGeolocationRepository, times(1)).save(any(CourierGeolocation.class));
        verify(distanceCalculator, never()).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void Should_CalculateDistanceAndPersistLocation_When_PreviousLocationsExist() {
        CreateCourierGeolocationRequest request = new CreateCourierGeolocationRequest(COURIER_ID, LAT, LNG);
        Courier courier = generateCourier();
        CourierGeolocation previousLocation = generateCourierGeolocation(500, 40.0, 29.0);
        List<CourierGeolocation> previousLogs = List.of(previousLocation);
        CourierGeolocation savedLocation = generateCourierGeolocation(1500.0, LAT, LNG);
        double calculatedDistance = 1000.0;

        when(courierService.findById(COURIER_ID)).thenReturn(courier);
        when(courierGeolocationRepository.findByCourierIdOrderByTimestampAsc(COURIER_ID)).thenReturn(previousLogs);
        when(distanceCalculator.calculateDistance(40.0, 29.0, LAT, LNG)).thenReturn(calculatedDistance);
        when(courierGeolocationRepository.save(any(CourierGeolocation.class))).thenReturn(savedLocation);

        CourierGeolocationResponse result = courierTrackingService.persistLocation(request);

        assertThat(result).isNotNull();
        assertEquals(COURIER_ID, result.courierId());
        assertThat(result.totalDistance()).isEqualTo(1500.0); // 500 (previous) + 1000 (calculated)

        verify(courierService, times(1)).findById(COURIER_ID);
        verify(courierGeolocationRepository, times(1)).findByCourierIdOrderByTimestampAsc(COURIER_ID);
        verify(distanceCalculator, times(1)).calculateDistance(40.0, 29.0, LAT, LNG);
        verify(courierGeolocationRepository, times(1)).save(any(CourierGeolocation.class));
    }

    @Test
    void Should_PublishEventToObservers_When_CourierIsWithinStoreRadius() {
        double storeLat = 42.0;
        double storeLng = 30.0;

        Courier courier = generateCourier();
        CourierGeolocation location = generateCourierGeolocation(100.0, LAT, LNG);
        Store store = generateStore();

        ReflectionTestUtils.setField(courierTrackingService, "observers", List.of(observer1));
        when(storeLoader.getStores()).thenReturn(List.of(store));
        when(distanceCalculator.calculateDistance(LAT, LNG, storeLat, storeLng)).thenReturn(DISTANCE_WITHIN_RADIUS);

        courierTrackingService.checkDistanceAndPublishEvent(courier, location);

        verify(storeLoader, times(1)).getStores();
        verify(distanceCalculator, times(1)).calculateDistance(LAT, LNG, storeLat, storeLng);
        verify(observer1, times(1)).handle(any(CourierNearStoreEvent.class));
    }


    @Test
    void Should_NotPublishEvent_When_CourierIsOutsideStoreRadius() {
        double storeLat = 42.0;
        double storeLng = 30.0;
        Courier courier = generateCourier();
        CourierGeolocation location = generateCourierGeolocation(100.0, LAT, LNG);
        Store store = generateStore();
        List<Store> stores = List.of(store);

        ReflectionTestUtils.setField(courierTrackingService, "observers", List.of(observer1));
        when(storeLoader.getStores()).thenReturn(stores);
        when(distanceCalculator.calculateDistance(LAT, LNG, storeLat, storeLng)).thenReturn(DISTANCE_OUTSIDE_RADIUS_RADIUS);

        courierTrackingService.checkDistanceAndPublishEvent(courier, location);

        verify(storeLoader, times(1)).getStores();
        verify(distanceCalculator, times(1)).calculateDistance(LAT, LNG, storeLat, storeLng);
        verify(observer1, never()).handle(any(CourierNearStoreEvent.class));
    }


    private CreateCourierGeolocationRequest generateCourierGeolocationRequest() {
        return new CreateCourierGeolocationRequest(COURIER_ID, LAT, LNG);
    }

    private Courier generateCourier() {
        Courier courier = new Courier();
        courier.setId(COURIER_ID);
        courier.setFullName("Can Okan");
        courier.setLicensePlate("34ABC123");
        return courier;
    }

    private CourierGeolocation generateCourierGeolocation(double totalDistance, double lat, double lng) {
        return CourierGeolocation.builder()
                .id(1L)
                .courier(generateCourier())
                .lat(lat)
                .lng(lng)
                .timestamp(LocalDateTime.now())
                .totalDistance(totalDistance)
                .build();
    }

    private Store generateStore() {
        return Store.builder().name("Store").lat(42.0).lng(30.0).build();
    }

}