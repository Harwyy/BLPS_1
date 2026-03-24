package com.blps.blps.dto;

import com.blps.blps.entity.enums.CourierStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierDto {
    private Long id;
    private String name;
    private String phone;
    private String city;
    private CourierStatus status;
}
