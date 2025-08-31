package com.jcardozo.easyspends.exceptions;

import org.springframework.http.HttpStatus;

public class ProductException extends RuntimeException {
    private final HttpStatus status;

    public ProductException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ProductException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
