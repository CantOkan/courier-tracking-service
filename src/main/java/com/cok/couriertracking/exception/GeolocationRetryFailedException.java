package com.cok.couriertracking.exception;

import lombok.Getter;

@Getter
public class GeolocationRetryFailedException extends RuntimeException {
    private final Long courierId;
    private final int retries;

    public GeolocationRetryFailedException(Long courierId, int retries, Throwable cause) {
        super("Failed to save courier location after " + retries + " retries for courier " + courierId, cause);
        this.courierId = courierId;
        this.retries = retries;
    }

}
