package me.exrates.adminservice.exceptions;

public class ExchangeApiException extends RuntimeException {

    public ExchangeApiException() {
        super();
    }

    public ExchangeApiException(String message) {
        super(message);
    }

    public ExchangeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}