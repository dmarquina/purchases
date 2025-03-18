package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail(String email);
    List<User> findByEmailAndPassword(String email, String password);

}
