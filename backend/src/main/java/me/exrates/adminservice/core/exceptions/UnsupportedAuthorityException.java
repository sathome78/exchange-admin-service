package me.exrates.adminservice.core.exceptions;

public class UnsupportedAuthorityException extends RuntimeException {
    public UnsupportedAuthorityException() {
    }

    public UnsupportedAuthorityException(String message) {
        super(message);
    }

    public UnsupportedAuthorityException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedAuthorityException(Throwable cause) {
        super(cause);
    }
}