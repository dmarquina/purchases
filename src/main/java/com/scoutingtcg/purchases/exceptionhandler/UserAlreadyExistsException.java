package com.scoutingtcg.purchases.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsException extends ResponseStatusException {

    public UserAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Error: No se pudo hacer la creación, este correo ya existe.");
    }


}