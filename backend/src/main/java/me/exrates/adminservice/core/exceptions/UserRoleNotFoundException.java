package me.exrates.adminservice.core.exceptions;

public class UserRoleNotFoundException extends NotFoundException {

    public UserRoleNotFoundException() {
        super();
    }

    public UserRoleNotFoundException(String message) {
        super(message);
    }

    public UserRoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRoleNotFoundException(Throwable cause) {
        super(cause);
    }
}