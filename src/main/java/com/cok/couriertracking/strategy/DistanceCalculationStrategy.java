package com.cok.couriertracking.strategy;

public interface DistanceCalculationStrategy {
    double calculateDistance(double lat1, double lng1, double lat2, double lng2);
}
