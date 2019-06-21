package me.exrates.adminservice.core.exceptions;

public class WalletPersistException extends ProcessingException {

    public WalletPersistException() {
        super();
    }

    public WalletPersistException(String message) {
        super(message);
    }

    public WalletPersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletPersistException(Throwable cause) {
        super(cause);
    }
}