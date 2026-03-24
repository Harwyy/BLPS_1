package com.blps.blps.repository;

import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.enums.RestaurantStatus;
import com.blps.blps.entity.enums.RestaurantType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findAllByAddress_CityAndStatus(String city, RestaurantStatus status);

    List<Restaurant> findTop3ByAddress_CityAndStatusOrderByRatingDesc(String city, RestaurantStatus status);

    List<Restaurant> findTop3ByAddress_CityAndTypeAndStatusOrderByRatingDesc(
            String city, RestaurantType type, RestaurantStatus status);
}
