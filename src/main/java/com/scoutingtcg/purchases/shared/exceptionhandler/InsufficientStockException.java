package com.scoutingtcg.purchases.shared.exceptionhandler;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
