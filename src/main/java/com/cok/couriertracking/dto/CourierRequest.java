package com.cok.couriertracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CourierRequest {

    @NotBlank
    @Length(min = 3, max = 64)
    private String fullName;

    @NotBlank
    @Pattern(
            regexp = "^(0[1-9]|[1-7][0-9]|8[01])[A-Z]{1,3}[0-9]{1,4}$",
            message = "Invalid Turkish license plate format"
    )
    private String licensePlate;
}
