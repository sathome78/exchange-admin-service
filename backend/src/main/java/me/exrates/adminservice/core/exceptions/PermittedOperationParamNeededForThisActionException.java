package me.exrates.adminservice.core.exceptions;

public class PermittedOperationParamNeededForThisActionException extends RuntimeException {

    public PermittedOperationParamNeededForThisActionException(String message) {
        super(message);
    }
}