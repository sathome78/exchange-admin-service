package me.exrates.adminservice.core.exceptions;

public class UnsupportedOperationTypeException extends RuntimeException {

    public UnsupportedOperationTypeException(int tupleId) {
        super("No such operation type " + tupleId);
    }

    public UnsupportedOperationTypeException(String message) {
        super(message);
    }
}