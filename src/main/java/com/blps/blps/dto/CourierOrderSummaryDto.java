package com.blps.blps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierOrderSummaryDto {
    private Long id;
    private String restaurantName;
    private String restaurantAddress;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String commentToCourier;
    private Boolean leaveAtDoor;
    private LocalDateTime createdAt;
}