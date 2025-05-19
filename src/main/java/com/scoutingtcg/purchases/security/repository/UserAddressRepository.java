package com.scoutingtcg.purchases.security.repository;

import com.scoutingtcg.purchases.security.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    @Query("SELECT ua FROM UserAddress ua JOIN ua.user u WHERE u.id = :userId AND ua.isActive = true")
    List<UserAddress> findActiveAddressesByUserId(@Param("userId") Long userId);
}

