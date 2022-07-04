package com.sirius.aath.backchannel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no invitation has this id")
public class ThereIsNoInvitationException extends RuntimeException{
    public ThereIsNoInvitationException(String message) {
        super(message);
    }
}