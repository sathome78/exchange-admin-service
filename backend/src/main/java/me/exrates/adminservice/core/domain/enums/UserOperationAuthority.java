package me.exrates.adminservice.core.domain.enums;

import me.exrates.adminservice.core.exceptions.UnsupportedAuthorityException;

import java.util.Arrays;

public enum UserOperationAuthority {
    INPUT(1),
    OUTPUT(2),
    TRANSFER(3),
    TRADING(4);

    public final int operationId;

    UserOperationAuthority(int operationId) {
        this.operationId = operationId;
    }

    public int getOperationId() {
        return operationId;
    }

    public static UserOperationAuthority convert(int operationId) {
        return Arrays.stream(UserOperationAuthority.values())
                .filter(auth -> auth.getOperationId() == operationId)
                .findAny().orElseThrow(() -> new UnsupportedAuthorityException("Unsupported operation of authority"));
    }
}