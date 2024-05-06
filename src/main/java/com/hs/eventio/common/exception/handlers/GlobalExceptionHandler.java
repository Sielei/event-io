package com.hs.eventio.common.exception.handlers;

import com.hs.eventio.common.exception.ResourceNotFoundException;
import com.hs.eventio.common.exception.UserAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e){
        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/errors/resource-not-found")
                .build().toUri();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(location);
        problemDetail.setProperty("timestamp", DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                .format(LocalDateTime.now()));
        return problemDetail;
    }

    @ExceptionHandler(value = {BadCredentialsException.class, UserAuthenticationException.class})
    ProblemDetail handleBadCredentialsException(RuntimeException e){
        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/errors/authentication-error")
                .build().toUri();
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("Authentication Error");
        problemDetail.setType(location);
        problemDetail.setProperty("timestamp", DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                .format(LocalDateTime.now()));
        return problemDetail;
    }
}
