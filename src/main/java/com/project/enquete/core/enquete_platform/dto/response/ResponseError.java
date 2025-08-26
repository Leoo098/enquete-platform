package com.project.enquete.core.enquete_platform.dto.response;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ResponseError(int stats, String message, List<ValidationError> errors) {

    public static ResponseError annotationExceptions(List<ValidationError> errors) {
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Erro de Validação", errors);
    }

    public static ResponseError conflict(List<ValidationError> errors) {
        return new ResponseError(HttpStatus.CONFLICT.value(), "Erro de Validação", errors);
    }

    public static ResponseError gone(String message){
        return new ResponseError(HttpStatus.GONE.value(), message, List.of());
    }
}
