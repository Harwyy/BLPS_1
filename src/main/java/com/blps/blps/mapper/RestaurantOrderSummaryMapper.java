package com.blps.blps.mapper;

import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.dto.RestaurantOrderSummaryDto;
import com.blps.blps.entity.Order;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantOrderSummaryMapper {
    public RestaurantOrderSummaryDto toSummaryDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getPrice()))
                .collect(Collectors.toList());

        return new RestaurantOrderSummaryDto(
                order.getId(),
                order.getUser().getName(),
                order.getUser().getPhone(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCommentToRestaurant(),
                order.getCreatedAt(),
                itemDtos);
    }
}
