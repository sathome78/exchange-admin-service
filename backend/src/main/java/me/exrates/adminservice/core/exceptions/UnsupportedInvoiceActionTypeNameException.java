package me.exrates.adminservice.core.exceptions;

public class UnsupportedInvoiceActionTypeNameException extends RuntimeException {

    public UnsupportedInvoiceActionTypeNameException(String message) {
        super(message);
    }
}