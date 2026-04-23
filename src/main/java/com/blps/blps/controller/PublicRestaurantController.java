package com.blps.blps.controller;

import com.blps.blps.dto.response.ProductResponse;
import com.blps.blps.dto.response.RestaurantsWithTopByTypeResponse;
import com.blps.blps.service.PublicRestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/restaurants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class PublicRestaurantController {

    private final PublicRestaurantService publicRestaurantService;

    @GetMapping
    public ResponseEntity<RestaurantsWithTopByTypeResponse> getRestaurantsWithTopByType(
            @RequestParam String city,
            @RequestParam(defaultValue = "false") boolean all) {
        return ResponseEntity.ok(publicRestaurantService.getTop3RestaurantsWithCategories(city, all));
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<ProductResponse>> getMenu(
            @PathVariable Long restaurantId, @PageableDefault(value = 3, page = 0) Pageable pageable) {
        return ResponseEntity.ok(publicRestaurantService.getMenu(restaurantId, pageable));
    }
}
