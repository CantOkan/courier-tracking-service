package com.cok.couriertracking.repository;

import com.cok.couriertracking.domain.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
}
