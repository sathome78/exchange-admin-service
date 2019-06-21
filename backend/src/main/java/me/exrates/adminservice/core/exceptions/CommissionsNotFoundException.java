package me.exrates.adminservice.core.exceptions;

public class CommissionsNotFoundException extends NotFoundException {

    public CommissionsNotFoundException() {
        super();
    }

    public CommissionsNotFoundException(String message) {
        super(message);
    }

    public CommissionsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommissionsNotFoundException(Throwable cause) {
        super(cause);
    }
}