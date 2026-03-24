package com.blps.blps.utils;

import com.blps.blps.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class DistanceCalculator {

    private static final double EARTH_RADIUS = 6371;
    private static final double MAX_DELIVERY_DISTANCE = 15.0;

    public double calculateDistance(Address from, Address to) {
        if (from.getLatitude() == null || from.getLongitude() == null ||
                to.getLatitude() == null || to.getLongitude() == null) {
            return 0;
        }
        return calculateDistance(from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude());
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public boolean isWithinDeliveryDistance(Address restaurantAddress, Address deliveryAddress) {
        double distance = calculateDistance(restaurantAddress, deliveryAddress);
        return distance <= MAX_DELIVERY_DISTANCE;
    }
}