package me.exrates.adminservice.core.exceptions;

public class InvoiceActionIsProhibitedForNotHolderException extends RuntimeException {

    public InvoiceActionIsProhibitedForNotHolderException(String message) {
        super(message);
    }
}