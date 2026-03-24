package com.blps.blps.dto.request;

import com.blps.blps.dto.AddressDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private Long userId;
    private Long restaurantId;
    private AddressDto address;
    private List<OrderItemRequest> items;
    private String commentToRestaurant;
    private String commentToCourier;
    private Boolean leaveAtDoor;
}
