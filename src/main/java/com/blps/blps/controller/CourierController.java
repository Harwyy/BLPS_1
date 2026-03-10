package com.blps.blps.controller;

import com.blps.blps.dto.CourierOrderResponse;
import com.blps.blps.service.CourierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courier")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @GetMapping("/active")
    public ResponseEntity<List<CourierOrderResponse>> getActiveOrders(@RequestHeader("X-Courier-Id") Long courierId) {
        List<CourierOrderResponse> orders = courierService.getActiveOrders(courierId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/pickup")
    public ResponseEntity<CourierOrderResponse> pickupOrder(
            @RequestHeader("X-Courier-Id") Long courierId, @RequestBody Long orderId) {
        CourierOrderResponse response = courierService.pickupOrder(courierId, orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/deliver")
    public ResponseEntity<CourierOrderResponse> deliverOrder(
            @RequestHeader("X-Courier-Id") Long courierId, @RequestBody Long orderId) {
        CourierOrderResponse response = courierService.deliverOrder(courierId, orderId);
        return ResponseEntity.ok(response);
    }
}
