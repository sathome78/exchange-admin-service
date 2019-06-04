package me.exrates.adminservice.domain.enums;

public enum DeviationStatus {

    MONITORED_IN_RANGE, //green color
    MONITORED_OUT_RANGE, //red color
    MONITORED_WITHOUT_RANGE, //yellow color
    NOT_MONITORED //without color
}