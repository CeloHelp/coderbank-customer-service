package com.coderbank.coderbank_costumer_service.exceptions;

public class TransactionServiceUnavailableException
        extends RuntimeException {


    public TransactionServiceUnavailableException(String message) {
        super(message);
    }

    public TransactionServiceUnavailableException(String message, Throwable cause) {

        super(message, cause);
    }



    }

