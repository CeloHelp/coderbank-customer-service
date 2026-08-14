package com.coderbank.coderbank_costumer_service.exceptions;

public class TransactionServiceUnavaliableException
        extends RuntimeException {


    public  TransactionServiceUnavaliableException(String message) {
        super(message);
    }

    public  TransactionServiceUnavaliableException(String message, Throwable cause) {

        super(message, cause);
    }



    }

