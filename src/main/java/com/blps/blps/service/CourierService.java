package com.blps.blps.service;

import com.blps.blps.dto.CourierOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.Address;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.utils.DistanceCalculator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final DistanceCalculator distanceCalculator;

    @Transactional
    public void assignCourierToOrder(Long orderId, Courier courier) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден: " + orderId));

        order.setCourier(courier);
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        courier.setActiveOrdersCount(courier.getActiveOrdersCount() + 1);
        if (courier.getActiveOrdersCount() >= 2) {
            courier.setStatus(CourierStatus.BUSY);
        }
        courierRepository.save(courier);
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse pickUpOrder(Long orderId, Long courierId) {
        Order order = orderRepository
                .findByIdAndCourierId(orderId, courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден или не принадлежит курьеру"));

        if (order.getStatus() != OrderStatus.READY) {
            throw new BusinessException(
                    "Заказ можно забрать только в статусе 'ГОТОВ'. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(), order.getStatus().name(), "Заказ принят курьером");
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse deliverOrder(Long orderId, Long courierId) {
        Order order = orderRepository
                .findByIdAndCourierId(orderId, courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден или не принадлежит курьеру"));

        if (order.getStatus() != OrderStatus.PICKED_UP) {
            throw new BusinessException(
                    "Заказ можно доставить только после того, как он был принят курьером. Текущий статус: "
                            + order.getStatus());
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        Courier courier = courierRepository
                .findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Курьер не найден"));
        courier.setActiveOrdersCount(courier.getActiveOrdersCount() - 1);
        if (courier.getActiveOrdersCount() < 2) {
            courier.setStatus(CourierStatus.AVAILABLE);
        }
        courierRepository.save(courier);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(), order.getStatus().name(), "Заказ доставлен");
    }

    public List<CourierOrderSummaryDto> getOrdersByStatus(Long courierId, OrderStatus status) {
        List<Order> orders = orderRepository.findByCourierIdAndStatus(courierId, status);
        return orders.stream().map(this::toSummaryDto).collect(Collectors.toList());
    }

    private CourierOrderSummaryDto toSummaryDto(Order order) {
        return new CourierOrderSummaryDto(
                order.getId(),
                order.getRestaurant().getName(),
                formatAddress(order.getRestaurant().getAddress()),
                formatAddress(order.getDeliveryAddress()),
                order.getTotalAmount(),
                order.getCommentToCourier(),
                order.getLeaveAtDoor(),
                order.getCreatedAt());
    }

    private String formatAddress(Address address) {
        return String.format("%s, %s %d", address.getCity(), address.getStreet(), address.getBuilding());
    }

    public Courier findBestCourierForOrder(Order order) {
        String city = order.getRestaurant().getAddress().getCity();
        Address restaurantAddress = order.getRestaurant().getAddress();

        List<Courier> availableCouriers = courierRepository.findByCityAndStatus(city, CourierStatus.AVAILABLE).stream()
                .filter(c -> c.getActiveOrdersCount() < 2)
                .collect(Collectors.toList());

        if (availableCouriers.isEmpty()) {
            throw new BusinessException("Нет доступных курьеров в городе " + city);
        }

        for (Courier courier : availableCouriers) {
            double distance = distanceCalculator.calculateDistance(
                    courier.getCurrentLatitude(), courier.getCurrentLongitude(),
                    restaurantAddress.getLatitude(), restaurantAddress.getLongitude());
            double score = calculateScore(courier, distance);
            courier.setScore(score);
        }

        availableCouriers.sort((c1, c2) -> Double.compare(c2.getScore(), c1.getScore()));

        return availableCouriers.get(0);
    }

    private double calculateScore(Courier courier, double distance) {
        double distanceWeight = 0.5;
        double ratingWeight = 0.3;
        double loadWeight = 0.2;

        double maxDistance = 10.0;
        double distanceScore = Math.max(0, maxDistance - distance) / maxDistance;
        double ratingScore = courier.getRating() / 5.0;
        double loadScore = 1.0 - (courier.getActiveOrdersCount() / 2.0);

        return distanceScore * distanceWeight + ratingScore * ratingWeight + loadScore * loadWeight;
    }
}
