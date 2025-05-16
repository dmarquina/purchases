package com.scoutingtcg.purchases.shared.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Integer errorCode;
    private String message;
}
