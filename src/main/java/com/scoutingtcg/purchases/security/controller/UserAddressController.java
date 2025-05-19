package com.scoutingtcg.purchases.security.controller;

import com.scoutingtcg.purchases.security.dto.CreateOrUpdateUserAddressRequest;
import com.scoutingtcg.purchases.security.dto.UserAddressDto;
import com.scoutingtcg.purchases.security.service.UserAddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping
    public List<UserAddressDto> list(@RequestParam Long userId) {
        return userAddressService.getAddresses(userId);
    }

    @PostMapping("/")
    public UserAddressDto create(@RequestBody CreateOrUpdateUserAddressRequest request) {
        return userAddressService.createAddress(request);
    }

    @PutMapping("/{addressId}")
    public UserAddressDto update(@PathVariable Long addressId,
                                 @RequestBody CreateOrUpdateUserAddressRequest request) {
        return userAddressService.updateAddress(addressId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
    }

    @PutMapping("/{addressId}/set-default")
    public void setDefault(@PathVariable Long addressId,
                           @RequestParam Long userId) {
        userAddressService.setDefault(userId, addressId);
    }
}
