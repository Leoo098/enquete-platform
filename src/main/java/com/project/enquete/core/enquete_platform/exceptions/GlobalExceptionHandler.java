package com.project.enquete.core.enquete_platform.exceptions;

import com.project.enquete.core.enquete_platform.controller.dto.response.ResponseError;
import com.project.enquete.core.enquete_platform.controller.dto.response.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleAnnotationExceptions(MethodArgumentNotValidException ex){
        System.out.println(ex.getBindingResult().getFieldErrors());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<ValidationError> listErrors = fieldErrors.stream()
                .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return ResponseError.annotationExceptions(listErrors);
    }

    @ExceptionHandler(UniqueFieldException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleUniqueFieldException(UniqueFieldException ex){
       return ResponseError.conflict(ex.getErrors());
    }

    @ExceptionHandler(ExpiredPollException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ResponseError handleExpiredPoll(ExpiredPollException ex){
        return ResponseError.gone(ex.getErrorMessage());
    }
}
