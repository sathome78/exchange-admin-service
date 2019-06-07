package me.exrates.adminservice.utils;

import org.apache.commons.lang3.StringUtils;

public class LogUtils {

    public static String stripDbUrl(String dbUrl) {
        return StringUtils.isNotBlank(dbUrl) && dbUrl.contains("?")
                ? dbUrl.substring(0, dbUrl.indexOf("?"))
                : "";
    }
}
