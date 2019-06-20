package me.exrates.adminservice.core.exceptions;

public class UnsupportedTransactionSourceTypeNameException extends RuntimeException {

    public UnsupportedTransactionSourceTypeNameException(String message) {
        super(message);
    }
}