package me.exrates.adminservice.controllers.handler;

import me.exrates.adminservice.domain.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorInfo HZExceptionHandler(Throwable exception) {
        String message = "Cause: " + exception.getLocalizedMessage();
        return new ErrorInfo(message, exception);
    }
}
