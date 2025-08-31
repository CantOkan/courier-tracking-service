package com.cok.couriertracking.exception;

import com.cok.couriertracking.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CourierNotFound.class})
    public ResponseEntity<Response<String>> handleCourierNotFound(CourierNotFound ex) {
        log.error("CourierNotFound", ex);
        return new ResponseEntity<>(new Response<>(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handle(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ResponseEntity<>(new Response<>(validationErrors), HttpStatus.BAD_REQUEST);
    }

}
