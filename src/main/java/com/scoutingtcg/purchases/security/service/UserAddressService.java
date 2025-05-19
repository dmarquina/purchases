package com.scoutingtcg.purchases.security.service;

import com.scoutingtcg.purchases.security.dto.CreateOrUpdateUserAddressRequest;
import com.scoutingtcg.purchases.security.dto.UserAddressDto;
import com.scoutingtcg.purchases.security.model.User;
import com.scoutingtcg.purchases.security.model.UserAddress;
import com.scoutingtcg.purchases.security.repository.UserAddressRepository;
import com.scoutingtcg.purchases.security.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    private final UserRepository userRepository;

    public UserAddressService(UserAddressRepository repository, UserRepository userRepository) {
        this.userAddressRepository = repository;
        this.userRepository = userRepository;
    }

    public List<UserAddressDto> getAddresses(Long userId) {
        return userAddressRepository.findActiveAddressesByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserAddressDto createAddress(CreateOrUpdateUserAddressRequest request) {
        if (request.setAsDefault()) {
            clearDefault(request.userId());
        }

        UserAddress entity = new UserAddress();
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setUser(user);
        entity.setFullName(request.recipientName());
        entity.setAddressLine(request.addressLine());
        entity.setCity(request.city());
        entity.setState(request.state());
        entity.setZipCode(request.zip());
        entity.setCountry(request.country());
        entity.setDefault(request.setAsDefault());

        return toDto(userAddressRepository.save(entity));
    }

    public UserAddressDto updateAddress(Long addressId, CreateOrUpdateUserAddressRequest request) {
        UserAddress entity = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (request.setAsDefault()) {
            clearDefault(request.userId());
        }

        entity.setFullName(request.recipientName());
        entity.setAddressLine(request.addressLine());
        entity.setCity(request.city());
        entity.setState(request.state());
        entity.setZipCode(request.zip());
        entity.setCountry(request.country());
        entity.setDefault(request.setAsDefault());

        if (request.setAsDefault()) {
            clearDefault(request.userId());
        }


        return toDto(userAddressRepository.save(entity));
    }

    public void deleteAddress(Long addressId) {
        UserAddress entity = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        entity.setActive(false);
        userAddressRepository.save(entity);
    }

    public void setDefault(Long userId, Long addressId) {
        clearDefault(userId);
        UserAddress entity = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        entity.setDefault(true);
        userAddressRepository.save(entity);
    }

    private void clearDefault(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findActiveAddressesByUserId(userId);
        for (UserAddress address : addresses) {
            address.setDefault(false);
        }
        userAddressRepository.saveAll(addresses);
    }

    private UserAddressDto toDto(UserAddress entity) {
        return new UserAddressDto(
                entity.getAddressId(),
                entity.getFullName(),
                entity.getAddressLine(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode(),
                entity.getCountry(),
                entity.isDefault()
        );
    }
}

