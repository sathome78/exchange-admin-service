package me.exrates.adminservice.domain;

import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

@ToString
public class ErrorInfo {
    public final String url;
    public final String cause;
    public final String detail;
    public String title;
    public String uuid;
    public Integer code;

    public ErrorInfo(CharSequence url, Throwable ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        String detail = ex.getLocalizedMessage() == null ? ex.getLocalizedMessage() : ex.getMessage();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = ex.getLocalizedMessage() == null ? detail : ex.getLocalizedMessage();
//        if (ex instanceof NgDashboardException) {
//            NgDashboardException custom = (NgDashboardException) ex;
//            if (custom.getCode() != null) {
//                this.code = custom.getCode();
//            }
//        } else if (ex instanceof OpenApiException) {
//            OpenApiException exception = (OpenApiException) ex;
//            this.title = exception.getTitle();
//        }
//        this.uuid = MDC.get("process.id");
    }
//
//    public ErrorInfo(CharSequence url, NgResponseException exception) {
//        this.url = url.toString();
//        this.cause = exception.getClass().getSimpleName();
//        this.detail =  exception.getMessage();
//        this.title = exception.getTitle();
//        this.code = exception.getHttpStatus().value();
//        this.uuid = MDC.get("process.id");
//    }
//
//    public ErrorInfo(CharSequence url, IeoException exception) {
//        this.url = url.toString();
//        this.cause = exception.getClass().getSimpleName();
//        this.detail =  exception.getMessage();
//        this.title = exception.getTitle();
//        this.code = exception.getHttpStatus().value();
//        this.uuid = MDC.get("process.id");
//    }

    public ErrorInfo(CharSequence url, Throwable ex, HttpStatus status) {
        this(url, ex);
        this.code = status.value();
    }

    public ErrorInfo(CharSequence url, Throwable ex, String reason) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = reason;
    }

    public ErrorInfo(CharSequence url, Throwable ex, String reason, String uuid) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = reason;
        this.uuid = uuid;
    }

    public ErrorInfo(CharSequence url, MethodArgumentNotValidException ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (!errors.isEmpty()) {
            this.detail = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(";\n"));
        } else {
            this.detail = ex.getLocalizedMessage();
        }

    }

    public ErrorInfo(CharSequence url, BindException ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();

        List<ObjectError> errors = ex.getAllErrors();
        if (!errors.isEmpty()) {
            this.detail = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(";\n"));
            System.out.println(detail);
        } else {
            this.detail = ex.getLocalizedMessage();
        }

    }

}
