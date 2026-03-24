package com.blps.blps.controller;

import com.blps.blps.dto.response.ProductResponse;
import com.blps.blps.dto.response.RestaurantsWithTopByTypeResponse;
import com.blps.blps.service.PublicRestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/restaurants")
@RequiredArgsConstructor
public class PublicRestaurantController {

    private final PublicRestaurantService publicRestaurantService;

    @GetMapping
    public ResponseEntity<RestaurantsWithTopByTypeResponse> getRestaurantsWithTopByType(@RequestParam String city) {
        return ResponseEntity.ok(publicRestaurantService.getTop3RestaurantsWithCategories(city));
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<ProductResponse>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(publicRestaurantService.getMenu(restaurantId));
    }
}
