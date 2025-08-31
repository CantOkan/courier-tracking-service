package com.cok.couriertracking.service;

import com.cok.couriertracking.domain.Store;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Component
public class StoreLoader {

    private final ObjectMapper objectMapper;
    private List<Store> stores = Collections.emptyList();

    public StoreLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("stores.json");
        try (InputStream is = resource.getInputStream()) {
            this.stores = objectMapper.readValue(is, new TypeReference<List<Store>>() {});
        }
    }

    public List<Store> getStores() {
        return stores;
    }
}
