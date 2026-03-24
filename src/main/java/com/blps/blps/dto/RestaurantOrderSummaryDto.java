package com.blps.blps.dto;

import com.blps.blps.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderSummaryDto {
    private Long id;
    private String userName;
    private String userPhone;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String commentToRestaurant;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
