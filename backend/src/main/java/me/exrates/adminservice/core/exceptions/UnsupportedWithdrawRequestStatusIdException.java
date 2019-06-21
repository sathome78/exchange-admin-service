package me.exrates.adminservice.core.exceptions;

public class UnsupportedWithdrawRequestStatusIdException extends RuntimeException {

    public UnsupportedWithdrawRequestStatusIdException(String message) {
        super(message);
    }
}