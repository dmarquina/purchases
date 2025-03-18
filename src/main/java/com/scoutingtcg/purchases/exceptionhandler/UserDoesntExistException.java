package com.scoutingtcg.purchases.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserDoesntExistException extends ResponseStatusException {

    public UserDoesntExistException() {
        super(HttpStatus.NOT_FOUND, "Error: Email o contraseña incorrectos.");
    }


}