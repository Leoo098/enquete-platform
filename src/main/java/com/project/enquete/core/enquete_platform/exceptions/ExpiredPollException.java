package com.project.enquete.core.enquete_platform.exceptions;

import lombok.Getter;

@Getter
public class ExpiredPollException extends RuntimeException{

    private final String errorMessage;

    public ExpiredPollException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
