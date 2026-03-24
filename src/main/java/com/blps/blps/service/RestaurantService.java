package com.blps.blps.service;

import com.blps.blps.dto.RestaurantOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.mapper.RestaurantOrderSummaryMapper;
import com.blps.blps.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final OrderRepository orderRepository;
    private final CourierService courierService;
    private final RestaurantOrderSummaryMapper restaurantOrderSummaryMapper;

    @Transactional
    public RestaurantOrCourierOrderActionResponse rejectOrder(Long orderId, Long restaurantId) {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Заказ не найден или не принадлежит ресторану с id " + restaurantId));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Заказ можно отклонить только в статусе ОПЛАЧЕН. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED_BY_REST);
        orderRepository.save(order);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(),
                order.getStatus().name(),
                "Заказ успешно отклонён"
        );
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse confirmOrder(Long orderId, Long restaurantId) {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Заказ не найден или не принадлежит ресторану с id " + restaurantId));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Заказ можно подтвердить только в статусе ОПЛАЧЕН. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);

        Courier bestCourier = courierService.findBestCourierForOrder(order);
        courierService.assignCourierToOrder(orderId, bestCourier);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(),
                order.getStatus().name(),
                "Заказ успешно подтверждён"
        );
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse markOrderReady(Long orderId, Long restaurantId) {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден или не принадлежит ресторану с id " + restaurantId));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new BusinessException("Заказ можно отметить как готовый только в статусе 'ГОТОВИТСЯ'. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(),
                order.getStatus().name(),
                "Заказ готов к выдаче курьеру"
        );
    }

    public List<RestaurantOrderSummaryDto> getOrdersByStatus(Long restaurantId, OrderStatus status) {
        List<Order> orders = orderRepository.findByRestaurantIdAndStatus(restaurantId, status);
        return orders.stream()
                .map(restaurantOrderSummaryMapper::toSummaryDto)
                .collect(Collectors.toList());
    }
}
