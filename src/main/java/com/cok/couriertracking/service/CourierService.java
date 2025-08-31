package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierRequest;
import com.cok.couriertracking.exception.CourierNotFound;
import com.cok.couriertracking.mapper.CourierMapper;
import com.cok.couriertracking.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    public Courier findById(Long id) {
        return courierRepository.findById(id).orElseThrow(() -> new CourierNotFound("Courier not found with given id!"));
    }

    public Courier create(CourierRequest courierRequest) {
        Courier courier = courierMapper.toEntity(courierRequest);
        Courier saved = courierRepository.save(courier);
        log.info("Courier created. CourierId: {}", courier.getId());
        return saved;
    }

    public void delete(Long id) {
        courierRepository.deleteById(id);
        log.info("Courier deleted. CourierId: {}", id);
    }
}
