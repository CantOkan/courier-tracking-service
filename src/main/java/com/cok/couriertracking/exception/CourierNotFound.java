package com.cok.couriertracking.exception;

public class CourierNotFound extends RuntimeException {
    public CourierNotFound(String message) {
        super(message);
    }
}
