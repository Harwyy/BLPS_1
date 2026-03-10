package com.blps.blps.controller;

import com.blps.blps.dto.RestaurantOrderResponse;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping("/active")
    public ResponseEntity<List<RestaurantOrderResponse>> getOrdersByStatus(
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {
        List<RestaurantOrderResponse> orders =
                restaurantService.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.SENT_TO_RESTAURANT);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/confirm")
    public ResponseEntity<RestaurantOrderResponse> confirmOrder(
            @RequestHeader("X-Restaurant-Id") Long restaurantId, @RequestBody Long orderId) {
        RestaurantOrderResponse response = restaurantService.confirmOrder(restaurantId, orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/decline")
    public ResponseEntity<RestaurantOrderResponse> declineOrder(
            @RequestHeader("X-Restaurant-Id") Long restaurantId, @RequestBody Long orderId) {
        RestaurantOrderResponse response = restaurantService.declineOrder(restaurantId, orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/ready")
    public ResponseEntity<Boolean> assignCourier(
            @RequestHeader("X-Restaurant-Id") Long restaurantId, @RequestBody Long orderId) {
        boolean response = restaurantService.markOrderAsReady(orderId, restaurantId);
        return ResponseEntity.ok(response);
    }
}
