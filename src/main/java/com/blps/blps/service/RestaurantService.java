package com.blps.blps.service;

import com.blps.blps.dto.RestaurantOrderResponse;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.mapper.RestaurantOrderResponseMapper;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final RestaurantOrderResponseMapper restaurantOrderResponseMapper;

    @Transactional(readOnly = true)
    public List<RestaurantOrderResponse> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status) {
        return orderRepository.findByRestaurantIdAndStatus(restaurantId, status).stream()
                .map(restaurantOrderResponseMapper::mapToRestaurantOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantOrderResponse confirmOrder(Long restaurantId, Long orderId) {
        Order order = findOrderAndValidateRestaurant(orderId, restaurantId);

        if (order.getStatus() != OrderStatus.SENT_TO_RESTAURANT) {
            throw new BusinessException("Невозможно подтвердить заказ в статусе " + order.getStatus());
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        return restaurantOrderResponseMapper.mapToRestaurantOrderResponse(order);
    }

    @Transactional
    public RestaurantOrderResponse declineOrder(Long restaurantId, Long orderId) {
        Order order = findOrderAndValidateRestaurant(orderId, restaurantId);

        if (order.getStatus() != OrderStatus.SENT_TO_RESTAURANT) {
            throw new BusinessException("Невозможно отклонить заказ в статусе " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED_BY_RESTAURANT);
        order = orderRepository.save(order);
        return restaurantOrderResponseMapper.mapToRestaurantOrderResponse(order);
    }

    @Transactional
    public boolean markOrderAsReady(Long orderId, Long restaurantId) {
        Order order = findOrderAndValidateRestaurant(orderId, restaurantId);

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException("Нельзя назначить курьера для заказа в статусе " + order.getStatus());
        }

        Courier courier = (Courier) courierRepository
                .findFirstByStatus(CourierStatus.AVAILABLE)
                .orElseThrow(() -> new BusinessException("Нет свободных курьеров"));

        order.setCourier(courier);
        order.setStatus(OrderStatus.COURIER_ASSIGNED);
        order.setAssignedAt(LocalDateTime.now());

        courier.setStatus(CourierStatus.BUSY);

        orderRepository.save(order);
        courierRepository.save(courier);
        return true;
    }

    private Order findOrderAndValidateRestaurant(Long orderId, Long restaurantId) {
        var order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new BusinessException("Заказ с id " + orderId + " не найден"));

        if (order.getRestaurant() == null) {
            throw new BusinessException("Заказ не привязан к ресторану");
        }
        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new BusinessException("Заказ не принадлежит указанному ресторану");
        }
        return order;
    }
}
