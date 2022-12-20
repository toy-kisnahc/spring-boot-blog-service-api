package com.kisnahc.blogservice.exception;

import com.kisnahc.blogservice.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.kisnahc.blogservice.controller")
public class ExceptionHandlerController {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> blogException(BlogApplicationException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .statusCode(exception.statusCode())
                .errorField(exception.getErrorField())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        Map<String, String> errorFields = new HashMap<>();

        for (FieldError fieldError : fieldErrors) {
            errorFields.put(fieldError.getField(), (String) fieldError.getRejectedValue());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getFieldError().getDefaultMessage())
                .statusCode(400)
                .errorField(errorFields)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
