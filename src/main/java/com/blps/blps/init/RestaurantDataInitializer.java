package com.blps.blps.init;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.enums.RestaurantType;
import com.blps.blps.repository.AddressRepository;
import com.blps.blps.repository.RestaurantRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(4)
public class RestaurantDataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) {
        List<Address> allAddresses = addressRepository.findAll();

        Map<String, List<Address>> addressesByCity =
                allAddresses.stream().collect(Collectors.groupingBy(Address::getCity));

        List<Restaurant> restaurants = new ArrayList<>();

        for (Map.Entry<String, List<Address>> entry : addressesByCity.entrySet()) {
            String city = entry.getKey();
            List<Address> cityAddresses = entry.getValue();

            restaurants.add(createRestaurant(
                    getRestaurantName(city, 0),
                    cityAddresses.get(0),
                    getPhoneForCity(city, 0),
                    RestaurantType.FAST_FOOD,
                    4.2));
            restaurants.add(createRestaurant(
                    getRestaurantName(city, 1),
                    cityAddresses.get(1),
                    getPhoneForCity(city, 1),
                    RestaurantType.ITALIAN,
                    4.5));
            restaurants.add(createRestaurant(
                    getRestaurantName(city, 2),
                    cityAddresses.get(2),
                    getPhoneForCity(city, 2),
                    RestaurantType.JAPANESE,
                    4.7));
        }

        restaurantRepository.saveAll(restaurants);
    }

    private String getRestaurantName(String city, int index) {
        switch (city) {
            case "Москва":
                return index == 0 ? "Бургерная №1" : index == 1 ? "Пиццерия Италия" : "Суши-бар Япония";
            case "Санкт-Петербург":
                return index == 0 ? "Петербургская бургерная" : index == 1 ? "Традиции Италии" : "Японский дворик";
            case "Казань":
                return index == 0 ? "Казанский фастфуд" : index == 1 ? "Пицца на Баумана" : "Суши-бар Казань";
            case "Новосибирск":
                return index == 0 ? "Сибирский бургер" : index == 1 ? "Итальянская лавка" : "Японский мост";
            case "Екатеринбург":
                return index == 0 ? "Уральский фастфуд" : index == 1 ? "Пицца на Малышева" : "Суши-бар Екатеринбург";
            default:
                return "Ресторан " + city + " " + (index + 1);
        }
    }

    private String getPhoneForCity(String city, int index) {
        String areaCode;
        switch (city) {
            case "Москва":
                areaCode = "495";
                break;
            case "Санкт-Петербург":
                areaCode = "812";
                break;
            case "Казань":
                areaCode = "843";
                break;
            case "Новосибирск":
                areaCode = "383";
                break;
            case "Екатеринбург":
                areaCode = "343";
                break;
            default:
                areaCode = "000";
        }
        return "+7(" + areaCode + ")" + String.format("%03d", index + 1) + "1234567";
    }

    private Restaurant createRestaurant(
            String name, Address address, String phone, RestaurantType type, double rating) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone(phone);
        restaurant.setType(type);
        restaurant.setRating(rating);
        return restaurant;
    }
}
