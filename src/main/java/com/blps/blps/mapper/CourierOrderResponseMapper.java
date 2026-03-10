package com.blps.blps.mapper;

import com.blps.blps.dto.AddressDto;
import com.blps.blps.dto.CourierOrderResponse;
import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.entity.Order;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourierOrderResponseMapper {

    private final AddressMapper addressMapper;

    public CourierOrderResponse mapToResponse(Order order) {
        CourierOrderResponse response = new CourierOrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus().name());

        AddressDto addressDto = addressMapper.mapToAddressDto(order.getDeliveryAddress());
        response.setDeliveryAddress(addressDto);

        response.setClientName(order.getUser().getName());
        response.setClientPhone(order.getUser().getPhone());

        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setCreatedAt(order.getCreatedAt());
        response.setAssignedAt(order.getAssignedAt());
        response.setPickedUpAt(order.getPickedUpAt());
        response.setDeliveredAt(order.getDeliveredAt());

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    OrderItemDto dto = new OrderItemDto();
                    dto.setProductId(item.getProduct().getId());
                    dto.setProductName(item.getProduct().getName());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice().doubleValue());
                    return dto;
                })
                .collect(Collectors.toList());
        response.setItems(itemDtos);

        return response;
    }
}
