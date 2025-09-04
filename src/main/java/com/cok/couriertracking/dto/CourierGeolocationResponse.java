package com.cok.couriertracking.dto;

import java.time.LocalDateTime;

public record CourierGeolocationResponse (
        Long courierId,
        Double lat,
        Double lng,
        LocalDateTime timestamp
) {
}
