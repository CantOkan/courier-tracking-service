package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierRequest;
import com.cok.couriertracking.exception.CourierNotFound;
import com.cok.couriertracking.mapper.CourierMapper;
import com.cok.couriertracking.repository.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierServiceTest {

    private static final Long COURIER_ID = 1L;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private CourierMapper courierMapper;

    @InjectMocks
    private CourierService courierService;

    @Test
    void Should_ReturnCourier_When_ValidIdExists() {
        when(courierRepository.findById(COURIER_ID)).thenReturn(Optional.of(generateCourier()));

        Courier result = courierService.findById(COURIER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(COURIER_ID);
        assertThat(result.getFullName()).isEqualTo("Can Okan");
        assertThat(result.getLicensePlate()).isEqualTo("34ABC123");

        verify(courierRepository, times(1)).findById(COURIER_ID);
    }

    @Test
    void Should_ThrowCourierNotFound_When_CourierIdDoesNotExist() {
        Long nonExistentId = 999L;
        when(courierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(CourierNotFound.class, () -> courierService.findById(nonExistentId));

        verify(courierRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void Should_ThrowCourierNotFound_When_NullIdProvided() {
        Long nullId = null;
        when(courierRepository.findById(nullId)).thenReturn(Optional.empty());

        assertThrows(CourierNotFound.class, () -> courierService.findById(nullId));

        verify(courierRepository, times(1)).findById(nullId);
    }

    @Test
    void Should_CreateAndReturnCourier_When_ValidCourierRequestProvided() {
        CourierRequest courierRequest = new CourierRequest();
        courierRequest.setFullName("Can Okan");
        courierRequest.setLicensePlate("34ABC123");

        Courier courier = generateCourier();
        when(courierMapper.toEntity(courierRequest)).thenReturn(courier);
        when(courierRepository.save(any(Courier.class))).thenReturn(courier);

        Courier result = courierService.create(courierRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("Can Okan");
        assertThat(result.getLicensePlate()).isEqualTo("34ABC123");

        verify(courierMapper, times(1)).toEntity(courierRequest);
        verify(courierRepository, times(1)).save(courier);
    }

    @Test
    void Should_DeleteCourier_When_ValidIdProvided() {
        Long courierId = 1L;
        doNothing().when(courierRepository).deleteById(courierId);

        courierService.delete(courierId);

        verify(courierRepository, times(1)).deleteById(courierId);
    }

    private Courier generateCourier() {
        Courier courier = new Courier();
        courier.setId(COURIER_ID);
        courier.setFullName("Can Okan");
        courier.setLicensePlate("34ABC123");
        return courier;
    }
}