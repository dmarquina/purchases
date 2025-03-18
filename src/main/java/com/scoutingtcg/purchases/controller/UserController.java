package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.User.CreateUserRequest;
import com.scoutingtcg.purchases.dto.User.LoggedUserResponse;
import com.scoutingtcg.purchases.dto.User.LoginUserRequest;
import com.scoutingtcg.purchases.model.User;
import com.scoutingtcg.purchases.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "The User Api")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Iniciar sesión", description = "Servicio para iniciar sesión")
    @PostMapping(value = "/auth")
    public LoggedUserResponse authUser(@RequestBody LoginUserRequest loginUserRequest) {
        User user = userService.getUser(loginUserRequest);
        LoggedUserResponse response = new LoggedUserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }

    @Operation(summary = "Crear usuario", description = "Servicio para crear usuario")
    @PostMapping(value = "/")
    public LoggedUserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = userService.createUser(createUserRequest);

        LoggedUserResponse response = new LoggedUserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }

}
