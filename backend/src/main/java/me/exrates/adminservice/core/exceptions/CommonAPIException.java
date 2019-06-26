package me.exrates.adminservice.core.exceptions;

import me.exrates.adminservice.domain.enums.ApiErrorsEnum;
import org.springframework.http.HttpStatus;

public class CommonAPIException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String title;

    private CommonAPIException(String message, String title, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.title = title;
    }

    public CommonAPIException(ApiErrorsEnum error, String message) {
        this(message, error.getTitle(), HttpStatus.BAD_REQUEST);
    }

}
