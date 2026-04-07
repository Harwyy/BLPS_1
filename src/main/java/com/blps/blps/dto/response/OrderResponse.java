package com.blps.blps.dto.response;

import com.blps.blps.dto.AddressDto;
import com.blps.blps.dto.CourierDto;
import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.dto.RestaurantDto;
import com.blps.blps.entity.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private RestaurantDto restaurant;
    private CourierDto courier;
    private AddressDto address;
    private OrderStatus status;
    private Integer estimatedDeliveryTime;
    private String commentToRestaurant;
    private String commentToCourier;
    private Boolean leaveAtDoor;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
}
