package com.blps.blps.init;

import com.blps.blps.entity.Address;
import com.blps.blps.repository.AddressRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class AddressDataInitializer implements CommandLineRunner {

    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) {
        List<Address> addresses = new ArrayList<>();

        double mskLat = 55.7558;
        double mskLon = 37.6173;
        addresses.addAll(createAddressesForCity(
                "Москва",
                mskLat,
                mskLon,
                new String[] {"Тверская", "Арбат", "Новый Арбат", "Кутузовский проспект", "Ленинградский проспект"},
                new int[] {7, 12, 15, 20, 25},
                new String[] {"3", "1", "5", "2", "4"},
                new String[] {"12", "5", "8", "15", "21"}));

        double spbLat = 59.9343;
        double spbLon = 30.3351;
        addresses.addAll(createAddressesForCity(
                "Санкт-Петербург",
                spbLat,
                spbLon,
                new String[] {
                    "Невский проспект",
                    "Московский проспект",
                    "Лиговский проспект",
                    "Большой проспект П.С.",
                    "Садовая улица"
                },
                new int[] {25, 30, 10, 5, 18},
                new String[] {"2", "4", "1", "3", "5"},
                new String[] {"45", "67", "23", "8", "91"}));

        double kznLat = 55.7964;
        double kznLon = 49.1089;
        addresses.addAll(createAddressesForCity(
                "Казань",
                kznLat,
                kznLon,
                new String[] {"Баумана", "Кремлёвская", "Петербургская", "Декабристов", "Чистопольская"},
                new int[] {15, 8, 22, 14, 33},
                new String[] {"1", "2", "3", "4", "5"},
                new String[] {"10", "20", "30", "40", "50"}));

        double nskLat = 55.0084;
        double nskLon = 82.9357;
        addresses.addAll(createAddressesForCity(
                "Новосибирск",
                nskLat,
                nskLon,
                new String[] {"Красный проспект", "Ленина", "Димитрова", "Кирова", "Гоголя"},
                new int[] {45, 12, 8, 21, 7},
                new String[] {"1", "2", "3", "4", "5"},
                new String[] {"110", "34", "56", "78", "99"}));

        double ekbLat = 56.8389;
        double ekbLon = 60.6057;
        addresses.addAll(createAddressesForCity(
                "Екатеринбург",
                ekbLat,
                ekbLon,
                new String[] {"Ленина", "Малышева", "Московская", "Белинского", "Тверитина"},
                new int[] {25, 31, 45, 12, 8},
                new String[] {"2", "4", "6", "8", "10"},
                new String[] {"112", "24", "36", "48", "60"}));

        addressRepository.saveAll(addresses);
    }

    private List<Address> createAddressesForCity(
            String city,
            double baseLat,
            double baseLon,
            String[] streets,
            int[] buildings,
            String[] floors,
            String[] apartments) {
        List<Address> list = new ArrayList<>();
        for (int i = 0; i < streets.length; i++) {
            Address address = new Address();
            address.setCity(city);
            address.setStreet(streets[i]);
            address.setBuilding(buildings[i]);
            address.setLatitude(baseLat + (Math.random() - 0.5) * 0.01);
            address.setLongitude(baseLon + (Math.random() - 0.5) * 0.01);
            address.setFloor(floors[i]);
            address.setApartment(apartments[i]);
            list.add(address);
        }
        return list;
    }
}
