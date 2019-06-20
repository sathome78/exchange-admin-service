package me.exrates.adminservice.utils;

import java.util.Objects;

public class AppConstants {

    public static final int LIMIT = 20;
    public static final int OFFSET = 0;


    public static int checkLimit(Integer limit) {
        if (Objects.isNull(limit) || limit < 1) {
            return LIMIT;
        }
        return limit;
    }

    public static int checkOffset(Integer offset) {
        if (Objects.isNull(offset)) {
            return OFFSET;
        }
        return offset;
    }
}
