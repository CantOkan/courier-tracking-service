package com.cok.couriertracking.repository;

import com.cok.couriertracking.domain.CourierGeolocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourierGeolocationRepository extends JpaRepository<CourierGeolocation, Long> {
    List<CourierGeolocation> findByCourierIdOrderByTimestampAsc(Long courierId);
}