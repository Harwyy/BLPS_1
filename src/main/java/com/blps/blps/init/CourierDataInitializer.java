package com.blps.blps.init;

import com.blps.blps.entity.Courier;
import com.blps.blps.repository.CourierRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(3)
public class CourierDataInitializer implements CommandLineRunner {

    private final CourierRepository courierRepository;

    @Override
    public void run(String... args) {
        List<Courier> couriers = List.of(
                createCourier("Сергей Васильев", "+79161234567", "Москва", 55.756, 37.617, 4.8, 0),
                createCourier("Анна Соколова", "+79172345678", "Москва", 55.752, 37.595, 4.9, 0),
                createCourier("Михаил Фёдоров", "+79183456789", "Москва", 55.752, 37.582, 4.7, 0),
                createCourier("Ольга Морозова", "+79194567890", "Москва", 55.758, 37.625, 4.6, 0),
                createCourier("Денис Новиков", "+79205678901", "Москва", 55.85, 37.6, 4.5, 0));

        courierRepository.saveAll(couriers);
    }

    private Courier createCourier(
            String name, String phone, String city, double lat, double lon, double rating, int activeOrders) {
        Courier courier = new Courier();
        courier.setName(name);
        courier.setPhone(phone);
        courier.setCity(city);
        courier.setCurrentLatitude(lat);
        courier.setCurrentLongitude(lon);
        courier.setRating(rating);
        courier.setActiveOrdersCount(activeOrders);
        return courier;
    }
}
