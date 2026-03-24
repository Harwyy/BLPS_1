package com.blps.blps.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
