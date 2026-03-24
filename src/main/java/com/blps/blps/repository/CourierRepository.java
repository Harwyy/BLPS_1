package com.blps.blps.repository;

import com.blps.blps.entity.Courier;
import com.blps.blps.entity.enums.CourierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByCityAndStatus(String city, CourierStatus status);
}
