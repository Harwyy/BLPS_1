package com.blps.blps.service;

import com.blps.blps.dto.request.OrderCreateRequest;
import com.blps.blps.dto.request.OrderItemRequest;
import com.blps.blps.dto.response.OrderResponse;
import com.blps.blps.entity.*;
import com.blps.blps.entity.enums.OrderPaymentStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.mapper.AddressMapper;
import com.blps.blps.mapper.OrderMapper;
import com.blps.blps.repository.*;
import com.blps.blps.utils.DeliveryTimeCalculator;
import com.blps.blps.utils.DistanceCalculator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final DistanceCalculator distanceCalculator;
    private final AddressMapper addressMapper;
    private final PaymentService paymentService;
    private final DeliveryTimeCalculator deliveryTimeCalculator;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Заказ не может быть пустым. Добавьте хотя бы одно блюдо.");
        }

        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + request.getUserId()));

        Restaurant restaurant = restaurantRepository
                .findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Ресторан не найден: " + request.getRestaurantId()));

        Address deliveryAddress;
        if (request.getAddress() != null && request.getAddress().getCity() != null) {
            deliveryAddress = addressMapper.toEntity(request.getAddress());
            deliveryAddress = addressRepository.save(deliveryAddress);
        } else {
            deliveryAddress = user.getAddress();
            if (deliveryAddress == null) {
                throw new BusinessException("У пользователя не указан адрес, и не передан адрес в запросе");
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(deliveryAddress);
        order.setCommentToRestaurant(request.getCommentToRestaurant());
        order.setCommentToCourier(request.getCommentToCourier());
        order.setLeaveAtDoor(request.getLeaveAtDoor());
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(OrderPaymentStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Товар не найден: " + itemRequest.getProductId()));

            if (!product.isAvailable()) {
                throw new BusinessException("Товар '" + product.getName() + "' временно недоступен");
            }

            if (!product.getRestaurant().getId().equals(restaurant.getId())) {
                throw new BusinessException(
                        "Товар '" + product.getName() + "' не принадлежит ресторану " + restaurant.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        Address restaurantAddress = restaurant.getAddress();
        boolean isWithinDistance = distanceCalculator.isWithinDeliveryDistance(restaurantAddress, deliveryAddress);

        if (!isWithinDistance) {
            savedOrder.setStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(OrderPaymentStatus.FAILED);
            orderRepository.save(savedOrder);
            return orderMapper.toResponse(savedOrder);
        }

        savedOrder.setStatus(OrderStatus.WAITING_PAYMENT);
        orderRepository.save(savedOrder);

        boolean paymentSuccess = paymentService.processPayment(savedOrder);
        if (!paymentSuccess) {
            savedOrder.setStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(OrderPaymentStatus.FAILED);
            orderRepository.save(savedOrder);
            throw new BusinessException("Оплата не прошла. Заказ отменён.");
        }

        savedOrder.setPaymentStatus(OrderPaymentStatus.PAID);
        savedOrder.setStatus(OrderStatus.PAID);
        orderRepository.save(savedOrder);

        Integer deliveryTime = deliveryTimeCalculator.calculateDeliveryTime(restaurantAddress, deliveryAddress);
        savedOrder.setEstimatedDeliveryTime(deliveryTime);
        orderRepository.save(savedOrder);

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        Order order =
                orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Заказ не найден: " + id));
        return orderMapper.toResponse(order);
    }
}
