package com.project.enquete.core.enquete_platform.exceptions;

import com.project.enquete.core.enquete_platform.controller.dto.response.ValidationError;
import lombok.Getter;

import java.util.List;

@Getter
public class UniqueFieldException extends RuntimeException{

    private final List<ValidationError> errors;

    public UniqueFieldException(String field, String errorMessage){
        this.errors = List.of(new ValidationError(field, errorMessage));
    }

    public UniqueFieldException(List<ValidationError> errors){
        this.errors = errors;
    }
}
