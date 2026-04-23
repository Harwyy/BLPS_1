package com.blps.blps.controller;

import com.blps.blps.dto.RestaurantOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.service.restaurantServices.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
public class RestaurantOrderController {

    private final RestaurantService restaurantService;

    @GetMapping("/pending")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantOrderSummaryDto>> getPendingOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getOrdersByStatus(restaurantId, OrderStatus.PAID));
    }

    @GetMapping("/confirmed")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantOrderSummaryDto>> getConfirmedOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getOrdersByStatus(restaurantId, OrderStatus.ASSIGNED));
    }

    @PostMapping("/{orderId}/reject")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> rejectOrder(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.rejectOrder(orderId, restaurantId));
    }

    @PostMapping("/{orderId}/confirm")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> confirmOrder(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.confirmOrder(orderId, restaurantId));
    }

    @PostMapping("/{orderId}/ready")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> markOrderReady(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.markOrderReady(orderId, restaurantId));
    }
}
