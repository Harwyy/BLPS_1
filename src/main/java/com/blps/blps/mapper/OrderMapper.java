package com.blps.blps.mapper;

import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.dto.response.OrderResponse;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.OrderItem;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final RestaurantMapper restaurantMapper;
    private final CourierMapper courierMapper;
    private final AddressMapper addressMapper;

    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setRestaurant(restaurantMapper.toDto(order.getRestaurant()));
        if (order.getCourier() != null) {
            response.setCourier(courierMapper.toDto(order.getCourier()));
        }
        response.setAddress(addressMapper.toDto(order.getDeliveryAddress()));
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setCommentToRestaurant(order.getCommentToRestaurant());
        response.setCommentToCourier(order.getCommentToCourier());
        response.setLeaveAtDoor(order.getLeaveAtDoor());
        response.setItems(order.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList()));
        response.setTotalAmount(order.getTotalAmount());
        return response;
    }

    OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice());
    }
}
