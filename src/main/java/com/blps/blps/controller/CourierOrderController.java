package com.blps.blps.controller;

import com.blps.blps.dto.CourierOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.service.courierServices.CourierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/couriers/{courierId}/orders")
public class CourierOrderController {

    private final CourierService courierService;

    @GetMapping("/assigned")
    @PreAuthorize("(hasRole('COURIER') and @courierSecurity.isCourierMatch(#courierId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<CourierOrderSummaryDto>> getAssignedOrders(@PathVariable Long courierId) {
        return ResponseEntity.ok(courierService.getOrdersByStatus(courierId, OrderStatus.ASSIGNED));
    }

    @GetMapping("/ready")
    @PreAuthorize("(hasRole('COURIER') and @courierSecurity.isCourierMatch(#courierId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<CourierOrderSummaryDto>> getReadyOrders(@PathVariable Long courierId) {
        return ResponseEntity.ok(courierService.getOrdersByStatus(courierId, OrderStatus.READY));
    }

    @GetMapping("/picked-up")
    @PreAuthorize("(hasRole('COURIER') and @courierSecurity.isCourierMatch(#courierId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<List<CourierOrderSummaryDto>> getPickedUpOrders(@PathVariable Long courierId) {
        return ResponseEntity.ok(courierService.getOrdersByStatus(courierId, OrderStatus.PICKED_UP));
    }

    @PostMapping("/{orderId}/pickup")
    @PreAuthorize("(hasRole('COURIER') and @courierSecurity.isCourierMatch(#courierId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> pickUpOrder(
            @PathVariable Long courierId, @PathVariable Long orderId) {
        return ResponseEntity.ok(courierService.pickUpOrder(orderId, courierId));
    }

    @PostMapping("/{orderId}/deliver")
    @PreAuthorize("(hasRole('COURIER') and @courierSecurity.isCourierMatch(#courierId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> deliverOrder(
            @PathVariable Long courierId, @PathVariable Long orderId) {
        return ResponseEntity.ok(courierService.deliverOrder(orderId, courierId));
    }
}
