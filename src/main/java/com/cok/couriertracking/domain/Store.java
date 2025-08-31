package com.cok.couriertracking.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Store {
    private String name;
    private double lat;
    private double lng;
}
