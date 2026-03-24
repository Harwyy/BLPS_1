package com.blps.blps.mapper;

import com.blps.blps.dto.AddressDto;
import com.blps.blps.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getLatitude(),
                address.getLongitude(),
                address.getFloor(),
                address.getApartment()
        );
    }

    public Address toEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        Address address = new Address();
        address.setCity(dto.getCity());
        address.setStreet(dto.getStreet());
        address.setBuilding(dto.getBuilding());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        address.setFloor(dto.getFloor());
        address.setApartment(dto.getApartment());
        return address;
    }
}
