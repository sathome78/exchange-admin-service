package me.exrates.adminservice.core.exceptions;

public class InvoiceActionIsProhibitedForCurrentContextException extends RuntimeException {

    public InvoiceActionIsProhibitedForCurrentContextException(String message) {
        super(message);
    }
}