package com.example.demo.exception;

import com.example.demo.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice
public class AccountServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomerNotActiveException.class})
    ResponseEntity<ApiError> customerNotActiveHandler(Exception exception, ServletWebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.EXPECTATION_FAILED);
        apiError.setErrors(Arrays.asList(exception.getMessage()));
        apiError.setMessage(exception.getMessage());
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler({AccountNotFoundException.class})
    ResponseEntity accountNotFoundHandler(Exception exception, ServletWebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.NOT_FOUND);
        apiError.setErrors(Arrays.asList(exception.getMessage()));
        apiError.setMessage(exception.getMessage());
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}
