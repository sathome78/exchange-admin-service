package me.exrates.adminservice.core.exceptions;

public class InvoiceActionIsProhibitedForCurrencyPermissionOperationException extends RuntimeException {

    public InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String message) {
        super(message);
    }
}