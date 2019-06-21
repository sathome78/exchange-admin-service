package me.exrates.adminservice.core.exceptions;

public class UnsupportedInvoiceStatusForActionException extends RuntimeException {

    public UnsupportedInvoiceStatusForActionException(String message) {
        super(message);
    }
}