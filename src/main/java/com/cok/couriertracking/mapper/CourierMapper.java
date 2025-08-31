package com.cok.couriertracking.mapper;

import com.cok.couriertracking.domain.Courier;
import com.cok.couriertracking.dto.CourierRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourierMapper {

    Courier toEntity(CourierRequest courierRequest);
}
