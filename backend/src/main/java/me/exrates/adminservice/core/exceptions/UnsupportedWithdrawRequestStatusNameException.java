package me.exrates.adminservice.core.exceptions;

public class UnsupportedWithdrawRequestStatusNameException extends RuntimeException {

    public UnsupportedWithdrawRequestStatusNameException(String message) {
        super(message);
    }
}