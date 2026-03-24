package com.blps.blps.mapper;

import com.blps.blps.dto.CourierDto;
import com.blps.blps.entity.Courier;
import org.springframework.stereotype.Component;

@Component
public class CourierMapper {

    public CourierDto toDto(Courier courier) {
        if (courier == null) {
            return null;
        }
        return new CourierDto(
                courier.getId(),
                courier.getName(),
                courier.getPhone(),
                courier.getCity(),
                courier.getStatus()
        );
    }
}