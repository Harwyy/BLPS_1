package com.blps.blps.dto.response;

import com.blps.blps.dto.RestaurantDto;
import com.blps.blps.entity.enums.RestaurantType;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantsWithTopByTypeResponse {
    private List<RestaurantDto> overallTop3;
    private Map<RestaurantType, List<RestaurantDto>> topByType;
}
