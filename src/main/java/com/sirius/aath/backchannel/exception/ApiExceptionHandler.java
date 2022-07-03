package com.sirius.aath.backchannel.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ThereIsNoInvitationException.class)
    protected ResponseEntity<ServiceException> handleThereIsNoInvitationException() {
        return new ResponseEntity<>(new ServiceException("There is no invitation has this id"), HttpStatus.NOT_FOUND);
    }

    @Data
    @AllArgsConstructor
    private static class ServiceException {
        private String message;
    }
}
