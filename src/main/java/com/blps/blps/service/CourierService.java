package com.blps.blps.service;

import com.blps.blps.dto.CourierOrderResponse;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.mapper.CourierOrderResponseMapper;
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
public class CourierService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final CourierOrderResponseMapper courierOrderResponseMapper;

    @Transactional(readOnly = true)
    public List<CourierOrderResponse> getActiveOrders(Long courierId) {
        List<OrderStatus> activeStatuses = List.of(OrderStatus.COURIER_ASSIGNED, OrderStatus.PICKED_UP);
        List<Order> orders = orderRepository.findByCourierIdAndStatusIn(courierId, activeStatuses);
        return orders.stream().map(courierOrderResponseMapper::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public CourierOrderResponse pickupOrder(Long courierId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));

        if (order.getCourier() == null) {
            throw new BusinessException("Заказ не назначен курьеру");
        }
        if (!order.getCourier().getId().equals(courierId)) {
            throw new BusinessException("Заказ не назначен этому курьеру");
        }
        if (order.getStatus() != OrderStatus.COURIER_ASSIGNED) {
            throw new BusinessException("Невозможно забрать заказ в статусе " + order.getStatus());
        }

        order.setStatus(OrderStatus.PICKED_UP);
        order.setPickedUpAt(LocalDateTime.now());
        order = orderRepository.save(order);

        return courierOrderResponseMapper.mapToResponse(order);
    }

    @Transactional
    public CourierOrderResponse deliverOrder(Long courierId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));

        if (order.getCourier() == null) {
            throw new BusinessException("Заказ не назначен курьеру");
        }
        if (!order.getCourier().getId().equals(courierId)) {
            throw new BusinessException("Заказ не назначен этому курьеру");
        }
        if (order.getStatus() != OrderStatus.PICKED_UP) {
            throw new BusinessException("Невозможно доставить заказ в статусе " + order.getStatus());
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        Courier courier = order.getCourier();
        courier.setStatus(CourierStatus.AVAILABLE);
        courierRepository.save(courier);

        order = orderRepository.save(order);
        return courierOrderResponseMapper.mapToResponse(order);
    }
}
