package me.exrates.adminservice.core.exceptions;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
    }

    public InvalidAmountException(String message) {
        super(message);
    }
}