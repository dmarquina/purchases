package com.scoutingtcg.purchases.security.service;

import com.scoutingtcg.purchases.security.dto.CreateUserRequest;
import com.scoutingtcg.purchases.security.dto.LoggedUserResponse;
import com.scoutingtcg.purchases.security.dto.LoginUserRequest;
import com.scoutingtcg.purchases.security.dto.UpdateUserRequest;
import com.scoutingtcg.purchases.security.exceptionhandler.UserAlreadyExistsException;
import com.scoutingtcg.purchases.security.exceptionhandler.UserDoesntExistException;
import com.scoutingtcg.purchases.security.model.Role;
import com.scoutingtcg.purchases.security.model.User;
import com.scoutingtcg.purchases.security.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

public LoggedUserResponse login(LoginUserRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BadCredentialsException("Invalid credentials");
    }

    String token = jwtService.generateToken(user.getEmail());
    return mapToLoggedUserResponse(user, token);
}

public LoggedUserResponse createUser(CreateUserRequest createUserRequest) {
    Optional<User> userOptional = userRepository.findByEmail(createUserRequest.getEmail());
    if (userOptional.isEmpty()) {
        User user = new User();
        user.setEmail(createUserRequest.getEmail());
        user.setName(createUserRequest.getName());
        user.setLastName(createUserRequest.getLastName());
        String encodedPassword = passwordEncoder.encode(createUserRequest.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER.name());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return mapToLoggedUserResponse(user, token);
    } else {
        User existingUser = userOptional.get();
        if (existingUser.getPassword() == null || existingUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(createUserRequest.getPassword());
            existingUser.setPassword(encodedPassword);
            userRepository.save(existingUser);

            String token = jwtService.generateToken(existingUser.getEmail());
            return mapToLoggedUserResponse(existingUser, token);
        } else {
            throw new UserAlreadyExistsException();
        }
    }
}

public LoggedUserResponse updateUser(UpdateUserRequest updateUserRequest) {
    Optional<User> userOptional = userRepository.findById(updateUserRequest.getUserId());
    if (userOptional.isPresent()) {
        User user = userOptional.get();
        user.setName(updateUserRequest.getName());
        user.setLastName(updateUserRequest.getLastName());
        user.setPhone(updateUserRequest.getPhone());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return mapToLoggedUserResponse(user, token);
    } else {
        throw new UserDoesntExistException();
    }
}

private LoggedUserResponse mapToLoggedUserResponse(User user, String token) {
    return new LoggedUserResponse(
            user.getUserId(),
            user.getName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole(),
            token
    );
}

}
