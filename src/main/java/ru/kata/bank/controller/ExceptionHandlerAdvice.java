package ru.kata.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kata.bank.model.exception.UnauthorizedException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> authParameterException(UnauthorizedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}