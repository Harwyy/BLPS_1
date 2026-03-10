package com.blps.blps.service;

import com.blps.blps.dto.*;
import com.blps.blps.entity.*;
import com.blps.blps.entity.enums.OrderPaymentStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.mapper.AddressMapper;
import com.blps.blps.mapper.OrderInfoResponseMapper;
import com.blps.blps.repository.*;
import com.blps.blps.util.DeliveryCalculator;
import com.blps.blps.validation.OrderValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final DeliveryCalculator deliveryCalculator;
    private final OrderValidator orderValidator;
    private final AddressMapper addressMapper;
    private final OrderInfoResponseMapper orderInfoResponseMapper;

    @Transactional(readOnly = true)
    public OrderInfoResponse getOrderById(Long id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException("Заказ с id " + id + " не найден"));
        OrderInfoResponse response = orderInfoResponseMapper.mapToOrderInfoResponse(order);
        response.setSuccess(true);
        response.setMessage("Заказ найден");
        return response;
    }

    @Transactional(readOnly = true)
    public OrderCheckResponse checkOrder(OrderRequest request) {
        OrderValidationResult validation = validateOrder(request, true);
        if (!validation.isSuccess()) {
            return buildErrorCheckResponse(validation.getMessage());
        }

        OrderCheckResponse response = new OrderCheckResponse();
        response.setSuccess(true);
        response.setMessage("Заказ может быть оформлен");
        response.setTotalAmount(validation.getTotal());
        response.setEstimatedDeliveryTime(validation.getDeliveryTime());
        response.setItems(validation.getValidatedItems());
        return response;
    }

    @Transactional
    public OrderInfoResponse confirmOrder(OrderRequest request) {
        OrderValidationResult validation = validateOrder(request, false);
        if (!validation.isSuccess()) {
            return buildErrorInfoResponse(validation.getMessage());
        }

        Order order = new Order();
        order.setUser(validation.getUser());
        order.setRestaurant(validation.getRestaurant());
        order.setDeliveryAddress(validation.getDeliveryAddress());
        order.setTotalAmount(BigDecimal.valueOf(validation.getTotal()));
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        order.setStatus(OrderStatus.SENT_TO_RESTAURANT);
        order.setEstimatedDeliveryTime(validation.getDeliveryTime());

        Order finalOrder = order;
        List<OrderItem> orderItems = validation.getValidatedItems().stream()
                .map(itemDto -> createOrderItem(itemDto, finalOrder))
                .collect(Collectors.toList());
        order.setItems(orderItems);

        order = orderRepository.save(order);
        processPayment(order);

        OrderInfoResponse successResponse = orderInfoResponseMapper.mapToOrderInfoResponse(order);
        successResponse.setSuccess(true);
        successResponse.setMessage("Заказ успешно создан");
        return successResponse;
    }

    private OrderValidationResult validateOrder(OrderRequest request, boolean isCheckOnly) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return OrderValidationResult.failure("Пользователь не найден");
        }

        Restaurant restaurant =
                restaurantRepository.findById(request.getRestaurantId()).orElse(null);
        if (restaurant == null) {
            return OrderValidationResult.failure("Ресторан не найден");
        }

        Optional<String> restaurantError = orderValidator.validateRestaurantStatus(restaurant);
        if (restaurantError.isPresent()) {
            return OrderValidationResult.failure(restaurantError.get());
        }

        Address deliveryAddress;
        if (request.getNewAddress() != null) {
            deliveryAddress = addressMapper.mapToAddress(request.getNewAddress());
            if (!isCheckOnly) {
                deliveryAddress = addressRepository.save(deliveryAddress);
            }
        } else {
            deliveryAddress = user.getAddress();
            if (deliveryAddress == null) {
                return OrderValidationResult.failure("У пользователя не указан адрес доставки");
            }
        }

        Address restaurantAddress = restaurant.getAddress();
        if (restaurantAddress == null) {
            return OrderValidationResult.failure("У ресторана не указан адрес");
        }

        double distance = deliveryCalculator.calculateDistance(
                restaurantAddress.getLatitude(), restaurantAddress.getLongitude(),
                deliveryAddress.getLatitude(), deliveryAddress.getLongitude());

        Optional<String> distanceError = orderValidator.validateDistance(distance);
        if (distanceError.isPresent()) {
            return OrderValidationResult.failure(distanceError.get());
        }

        OrderValidator.ValidationResult productValidation =
                orderValidator.validateProducts(request.getItems(), restaurant.getId());
        if (!productValidation.isSuccess()) {
            return OrderValidationResult.failure(productValidation.getErrorMessage());
        }

        double total = productValidation.getValidatedItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        int deliveryTime = deliveryCalculator.calculateDeliveryTime(distance);

        return OrderValidationResult.success(
                user,
                restaurant,
                deliveryAddress,
                distance,
                productValidation.getValidatedItems(),
                total,
                deliveryTime);
    }

    private OrderCheckResponse buildErrorCheckResponse(String message) {
        OrderCheckResponse response = new OrderCheckResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    private OrderInfoResponse buildErrorInfoResponse(String message) {
        OrderInfoResponse response = new OrderInfoResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    private OrderItem createOrderItem(OrderItemDto itemDto, Order order) {
        Product product = productRepository
                .findById(itemDto.getProductId())
                .orElseThrow(() -> new BusinessException("Товар не найден после валидации"));
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(itemDto.getQuantity());
        item.setPrice(BigDecimal.valueOf(itemDto.getPrice()));
        return item;
    }

    private void processPayment(Order order) {
        order.setPaymentStatus(OrderPaymentStatus.PAID);
        orderRepository.save(order);
    }
}
