package com.scoutingtcg.purchases.security.controller;

import com.scoutingtcg.purchases.security.dto.CreateUserRequest;
import com.scoutingtcg.purchases.security.dto.LoggedUserResponse;
import com.scoutingtcg.purchases.security.dto.LoginUserRequest;
import com.scoutingtcg.purchases.security.dto.UpdateUserRequest;
import com.scoutingtcg.purchases.security.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "The User Api")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
