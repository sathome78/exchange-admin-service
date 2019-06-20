package me.exrates.adminservice.core.exceptions;

public class AvailableForCurrentContextParamNeededForThisActionException extends RuntimeException {

    public AvailableForCurrentContextParamNeededForThisActionException(String message) {
        super(message);
    }
}