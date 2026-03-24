package com.blps.blps.dto;

import com.blps.blps.entity.enums.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private Long id;
    private String name;
    private RestaurantType type;
    private Double rating;
    private String phone;
    private String city;
    private String street;
    private Integer building;
}
