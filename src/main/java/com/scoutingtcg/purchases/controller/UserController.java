package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.User.CreateUserRequest;
import com.scoutingtcg.purchases.dto.User.LoggedUserResponse;
import com.scoutingtcg.purchases.dto.User.LoginUserRequest;
import com.scoutingtcg.purchases.dto.User.UpdateUserRequest;
import com.scoutingtcg.purchases.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return userService.login(loginUserRequest);

    }

    @PostMapping(value = "/")
    public LoggedUserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(createUserRequest);
    }


    @PutMapping(value = "/")
    public LoggedUserResponse updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(updateUserRequest);
    }

}
