package com.blps.blps.dto;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.User;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderValidationResult {
    private final boolean success;
    private final String message;
    private final User user;
    private final Restaurant restaurant;
    private final Address deliveryAddress;
    private final double distance;
    private final List<OrderItemDto> validatedItems;
    private final double total;
    private final int deliveryTime;

    private OrderValidationResult(
            User user,
            Restaurant restaurant,
            Address deliveryAddress,
            double distance,
            List<OrderItemDto> validatedItems,
            double total,
            int deliveryTime) {
        this.success = true;
        this.message = null;
        this.user = user;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.distance = distance;
        this.validatedItems = validatedItems;
        this.total = total;
        this.deliveryTime = deliveryTime;
    }

    private OrderValidationResult(String message) {
        this.success = false;
        this.message = message;
        this.user = null;
        this.restaurant = null;
        this.deliveryAddress = null;
        this.distance = 0;
        this.validatedItems = null;
        this.total = 0;
        this.deliveryTime = 0;
    }

    public static OrderValidationResult success(
            User user,
            Restaurant restaurant,
            Address deliveryAddress,
            double distance,
            List<OrderItemDto> validatedItems,
            double total,
            int deliveryTime) {
        return new OrderValidationResult(
                user, restaurant, deliveryAddress, distance, validatedItems, total, deliveryTime);
    }

    public static OrderValidationResult failure(String message) {
        return new OrderValidationResult(message);
    }
}
