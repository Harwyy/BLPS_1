package com.blps.blps.mapper;

import com.blps.blps.dto.RestaurantDto;
import com.blps.blps.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public RestaurantDto toDto(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }
        return new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getType(),
                restaurant.getRating(),
                restaurant.getPhone(),
                restaurant.getAddress().getCity(),
                restaurant.getAddress().getStreet(),
                restaurant.getAddress().getBuilding());
    }
}
