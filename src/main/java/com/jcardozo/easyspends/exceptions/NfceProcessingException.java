package com.jcardozo.easyspends.exceptions;

public class NfceProcessingException extends RuntimeException {
    public NfceProcessingException(String message) {
        super(message);
    }

    public NfceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
