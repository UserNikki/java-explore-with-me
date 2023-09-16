package ru.practicum.ewmservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.model.ApiError;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError illegalArgException(final IllegalArgumentException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(String.valueOf(e.getCause()))
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(final ConstraintViolationException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(String.valueOf(e.getCause()))
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError illegalArgException(final IllegalStateException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(String.valueOf(e.getCause()))
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(final MethodArgumentNotValidException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(String.valueOf(e.getCause()))
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError validationException(final ValidationException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.toString())
                .reason(String.valueOf(e.getCause()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundException(final NotFoundException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.toString())
                .reason(String.valueOf(e.getCause()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    public ApiError notFoundException(final Exception e) {
        return ApiError.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .reason(Arrays.toString(e.getStackTrace()))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
