package me.exrates.adminservice.domain.enums;

public enum ApiErrorsEnum {

    UNREQUESTED_USER_ERROR("UNREQUESTED_USER_ERROR");


    private final String title;

    ApiErrorsEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
