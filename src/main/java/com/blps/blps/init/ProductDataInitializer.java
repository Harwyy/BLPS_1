package com.blps.blps.init;

import com.blps.blps.entity.Product;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.repository.ProductRepository;
import com.blps.blps.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(5)
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public void run(String... args) {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        List<Product> products = new ArrayList<>();

        List<Restaurant> moscowRestaurants = allRestaurants.stream()
                .filter(r ->
                        r.getAddress() != null && "Москва".equals(r.getAddress().getCity()))
                .toList();

        for (Restaurant restaurant : moscowRestaurants) {
            products.addAll(createProductsForRestaurant(restaurant));
        }

        productRepository.saveAll(products);
    }

    private List<Product> createProductsForRestaurant(Restaurant restaurant) {
        List<Product> products = new ArrayList<>();

        switch (restaurant.getType()) {
            case FAST_FOOD:
                products.add(createProduct(restaurant, "Чизбургер", "Сочная котлета, сыр, соус", 250.00, true));
                products.add(createProduct(restaurant, "Гамбургер", "Классический бургер", 200.00, true));
                products.add(createProduct(restaurant, "Картошка фри", "Хрустящая картошка", 120.00, true));
                products.add(createProduct(restaurant, "Кола", "Напиток", 100.00, true));
                products.add(createProduct(restaurant, "Бургер с беконом", "Временно недоступен", 320.00, false));
                break;
            case ITALIAN:
                products.add(createProduct(restaurant, "Маргарита", "Томатный соус, моцарелла", 450.00, true));
                products.add(createProduct(restaurant, "Пепперони", "Острая колбаса", 550.00, true));
                products.add(createProduct(restaurant, "Четыре сыра", "Сливочный вкус", 600.00, true));
                products.add(createProduct(restaurant, "Гавайская", "Курица, ананас", 500.00, true));
                products.add(createProduct(restaurant, "Тирамису", "Десерт", 320.00, true));
                break;
            case JAPANESE:
                products.add(createProduct(restaurant, "Филадельфия", "Лосось, сливочный сыр", 400.00, true));
                products.add(createProduct(restaurant, "Калифорния", "Краб, авокадо", 380.00, true));
                products.add(createProduct(restaurant, "Унаги маки", "Угорь, соус", 420.00, true));
                products.add(createProduct(restaurant, "Сет 'Самурай'", "Ассорти роллов", 1200.00, true));
                products.add(createProduct(restaurant, "Имбирь", "Маринованный", 50.00, true));
                break;
            default:
                break;
        }

        return products;
    }

    private Product createProduct(
            Restaurant restaurant, String name, String description, double price, boolean available) {
        Product product = new Product();
        product.setRestaurant(restaurant);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setAvailable(available);
        return product;
    }
}
