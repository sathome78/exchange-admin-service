package me.exrates.adminservice.core.exceptions;

public class UnsupportedTransactionSourceTypeIdException extends RuntimeException {

    public UnsupportedTransactionSourceTypeIdException(String message) {
        super(message);
    }
}