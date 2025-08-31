package com.cok.couriertracking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private String message;
    private T data;
    private Map<String, String> validationErrors;

    public Response(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public Response(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(T data) {
        this.data = data;
    }
}
