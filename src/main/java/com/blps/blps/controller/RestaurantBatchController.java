package com.blps.blps.controller;

import com.blps.blps.dto.request.BatchOrderRequest;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.service.restaurantServices.RestaurantBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/batch")
public class RestaurantBatchController  {

    private final RestaurantBatchService restaurantBatchService;

    @PostMapping("/ready")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantOrCourierOrderActionResponse>> markOrdersReady(
            @PathVariable Long restaurantId,
            @RequestBody BatchOrderRequest request) {
        return ResponseEntity.ok(restaurantBatchService.markMultipleOrdersReady(restaurantId, request.getOrderIds()));
    }

    @PostMapping("/assign-couriers")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantOrCourierOrderActionResponse>> assignCouriers(
            @PathVariable Long restaurantId,
            @RequestBody BatchOrderRequest request) {
        return ResponseEntity.ok(restaurantBatchService.assignCouriersForMultipleOrders(restaurantId, request.getOrderIds()));
    }
}
