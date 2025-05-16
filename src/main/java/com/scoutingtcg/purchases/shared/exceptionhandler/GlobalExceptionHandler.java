package com.scoutingtcg.purchases.shared.exceptionhandler;

import com.scoutingtcg.purchases.security.exceptionhandler.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatusCode().value(), ex.getReason());
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleStockError(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
