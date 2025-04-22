package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.User.CreateUserRequest;
import com.scoutingtcg.purchases.dto.User.LoggedUserResponse;
import com.scoutingtcg.purchases.dto.User.LoginUserRequest;
import com.scoutingtcg.purchases.dto.User.UpdateUserRequest;
import com.scoutingtcg.purchases.model.User;
import com.scoutingtcg.purchases.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "The User Api")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/login")
    public LoggedUserResponse login(@RequestBody LoginUserRequest loginUserRequest) {
        User user = userService.getUser(loginUserRequest);
        LoggedUserResponse response = new LoggedUserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setToken(UUID.randomUUID().toString());
        return response;
    }

    @PostMapping(value = "/")
    public LoggedUserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = userService.createUser(createUserRequest);

        LoggedUserResponse response = new LoggedUserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setToken(UUID.randomUUID().toString());

        return response;
    }


    @PutMapping(value = "/")
    public LoggedUserResponse updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.updateUser(updateUserRequest);

        LoggedUserResponse response = new LoggedUserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setToken(UUID.randomUUID().toString());

        return response;
    }

}
