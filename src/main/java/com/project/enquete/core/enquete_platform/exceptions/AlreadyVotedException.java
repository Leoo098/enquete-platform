package com.project.enquete.core.enquete_platform.exceptions;

import lombok.Getter;

@Getter
public class AlreadyVotedException extends RuntimeException{

    private final String errorMessage;

    public AlreadyVotedException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
