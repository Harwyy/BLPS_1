package com.blps.blps.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrCourierOrderActionResponse {
    private Long orderId;
    private String newStatus;
    private String message;
}