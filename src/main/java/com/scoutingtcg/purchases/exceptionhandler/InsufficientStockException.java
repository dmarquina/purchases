package com.scoutingtcg.purchases.exceptionhandler;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
