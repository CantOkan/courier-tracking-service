package com.cok.couriertracking.controller;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierRequest;
import com.cok.couriertracking.dto.Response;
import com.cok.couriertracking.service.CourierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/courier")
public class CourierController {

    private final CourierService courierService;

    @PostMapping
    public ResponseEntity<Response<Courier>> create(@RequestBody @Valid CourierRequest request) {
        Courier courier = courierService.create(request);
        return new ResponseEntity<>(new Response<>("Courier created successfully", courier), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Courier>> getById(@PathVariable @NotNull Long id) {
        Courier courier = courierService.findById(id);
        return new ResponseEntity<>(new Response<>(courier), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Courier>> delete(@PathVariable @NotNull Long id) {
        courierService.delete(id);
        return new ResponseEntity<>(new Response<>("Courier deleted"), HttpStatus.OK);
    }
}
