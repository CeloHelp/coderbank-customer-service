package com.coderbank.coderbank_costumer_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(TransactionServiceUnavaliableException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionServiceUnavaliableException(TransactionServiceUnavaliableException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                503,
                "Service Unavailable",
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.
                status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}
