package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.User.CreateUserRequest;
import com.scoutingtcg.purchases.dto.User.LoginUserRequest;
import com.scoutingtcg.purchases.dto.User.UpdateUserRequest;
import com.scoutingtcg.purchases.exceptionhandler.UserAlreadyExistsException;
import com.scoutingtcg.purchases.exceptionhandler.UserDoesntExistException;
import com.scoutingtcg.purchases.model.Role;
import com.scoutingtcg.purchases.model.User;
import com.scoutingtcg.purchases.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    public User getUser(LoginUserRequest loginUserRequest) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        Optional<User> userOptional = userRepository.findByEmailAndPassword(email,password).stream().findFirst();
        if(userOptional.isPresent()){
            return userOptional.get();
        } else {
            throw new UserDoesntExistException();
        }
    }

    public User createUser(CreateUserRequest createUserRequest) {
        Optional<User> userOptional = userRepository.findByEmail(createUserRequest.getEmail());
        if(userOptional.isEmpty()){
            User user = new User();
            user.setEmail(createUserRequest.getEmail());
            user.setName(createUserRequest.getName());
            user.setLastName(createUserRequest.getLastName());
            user.setPassword(createUserRequest.getPassword());
            user.setRole(Role.USER.name());
            return userRepository.save(user);
        } else {
            throw new UserAlreadyExistsException();
        }
    }

    public User updateUser(UpdateUserRequest updateUserRequest) {
        Optional<User> userOptional = userRepository.findById(updateUserRequest.getUserId());
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setName(updateUserRequest.getName());
            user.setLastName(updateUserRequest.getLastName());
            user.setPhone(updateUserRequest.getPhone());
            return userRepository.save(user);
        } else {
            throw new UserDoesntExistException();
        }
    }

}
