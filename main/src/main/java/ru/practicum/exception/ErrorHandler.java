package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    //error json: errors    message    reason    status    timestamp

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflictException(final ConflictException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Значения не должны совпадать.", HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "handleNotFoundException", HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectParameterException(final ValidationException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "handleNotFoundException", HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestValidationExceptions(ConstraintViolationException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "handleNotFoundException", HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> annotationValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            log.error("В {} возникла ошибка: {}", fieldName, errorMessage);
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError propertyValueException(final RuntimeException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Ошибки в запросе.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }


}