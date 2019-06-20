package me.exrates.adminservice.core.exceptions;

public class AuthorisedUserIsHolderParamNeededForThisActionException extends RuntimeException {

    public AuthorisedUserIsHolderParamNeededForThisActionException(String message) {
        super(message);
    }
}