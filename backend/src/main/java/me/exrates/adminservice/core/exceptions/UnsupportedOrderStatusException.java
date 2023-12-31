package me.exrates.adminservice.core.exceptions;

public class UnsupportedOrderStatusException extends RuntimeException {

    public final int orderStatusId;

    public UnsupportedOrderStatusException(int tupleId) {
        super("No such order status " + tupleId);
        this.orderStatusId = tupleId;
    }
}