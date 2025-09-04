package com.cok.couriertracking.controller;

import com.cok.couriertracking.dto.CourierGeolocationResponse;
import com.cok.couriertracking.dto.CreateCourierGeolocationRequest;
import com.cok.couriertracking.dto.Response;
import com.cok.couriertracking.service.CourierGeolocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/couriers/geolocations")
public class CourierGeoLocationController {

    private final CourierGeolocationService courierGeolocationService;

    @PostMapping
    public ResponseEntity<Response<CourierGeolocationResponse>> addLocation(@RequestBody @Valid CreateCourierGeolocationRequest request) {
        CourierGeolocationResponse courierGeolocationResponse = courierGeolocationService.createLocation(request);
        return new ResponseEntity<>(new Response<>(courierGeolocationResponse), HttpStatus.OK);
    }

}
