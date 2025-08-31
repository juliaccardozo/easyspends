package com.jcardozo.easyspends.exceptions;

import org.springframework.http.HttpStatus;

public class PurchaseException extends Throwable {
    public PurchaseException(String message) {
        super(message);
    }

    public PurchaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public PurchaseException(HttpStatus httpStatus, String s) {}
}
