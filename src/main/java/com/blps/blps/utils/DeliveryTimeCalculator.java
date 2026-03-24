package com.blps.blps.utils;

import com.blps.blps.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryTimeCalculator {

    private final DistanceCalculator distanceCalculator;

    public Integer calculateDeliveryTime(Address restaurantAddress, Address deliveryAddress) {
        double distance = distanceCalculator.calculateDistance(restaurantAddress, deliveryAddress);
        int time = 15 + (int) (distance * 5);
        return time;
    }
}
