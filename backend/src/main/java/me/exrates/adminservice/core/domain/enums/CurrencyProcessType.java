package me.exrates.adminservice.core.domain.enums;

import me.exrates.adminservice.core.exceptions.UnsupportedProcessTypeException;

import java.util.Arrays;

public enum CurrencyProcessType {

    FIAT, CRYPTO;

    public static CurrencyProcessType convert(String type) {
        return Arrays.stream(CurrencyProcessType.values())
                .filter(val -> val.name().equals(type))
                .findAny().orElseThrow(() -> new UnsupportedProcessTypeException(type));
    }

    @Override
    public String toString() {
        return this.name();
    }
}