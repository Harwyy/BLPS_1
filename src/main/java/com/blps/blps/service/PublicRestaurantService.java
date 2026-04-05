package com.blps.blps.service;

import com.blps.blps.dto.RestaurantDto;
import com.blps.blps.dto.response.ProductResponse;
import com.blps.blps.dto.response.RestaurantsWithTopByTypeResponse;
import com.blps.blps.entity.Product;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.enums.RestaurantStatus;
import com.blps.blps.entity.enums.RestaurantType;
import com.blps.blps.mapper.ProductMapper;
import com.blps.blps.mapper.RestaurantMapper;
import com.blps.blps.repository.ProductRepository;
import com.blps.blps.repository.RestaurantRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final RestaurantMapper restaurantMapper;
    private final ProductMapper productMapper;

    public RestaurantsWithTopByTypeResponse getTop3RestaurantsWithCategories(String city) {
        List<Restaurant> overallTop3 =
                restaurantRepository.findTop3ByAddress_CityAndStatusOrderByRatingDesc(city, RestaurantStatus.ACTIVE);

        List<Restaurant> allInCity = restaurantRepository.findAllByAddress_CityAndStatus(city, RestaurantStatus.ACTIVE);
        Set<RestaurantType> types = allInCity.stream().map(Restaurant::getType).collect(Collectors.toSet());

        Map<RestaurantType, List<RestaurantDto>> topByType = new LinkedHashMap<>();
        for (RestaurantType type : types) {
            List<Restaurant> top3ByType = restaurantRepository.findTop3ByAddress_CityAndTypeAndStatusOrderByRatingDesc(
                    city, type, RestaurantStatus.ACTIVE);
            topByType.put(type, top3ByType.stream().map(restaurantMapper::toDto).collect(Collectors.toList()));
        }

        return new RestaurantsWithTopByTypeResponse(
                overallTop3.stream().map(restaurantMapper::toDto).collect(Collectors.toList()), topByType);
    }

    public List<ProductResponse> getMenu(Long restaurantId) {
        List<Product> products = productRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
        return products.stream().map(productMapper::toResponse).collect(Collectors.toList());
    }
}
