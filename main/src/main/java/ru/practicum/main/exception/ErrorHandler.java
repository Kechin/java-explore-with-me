package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    //error json: errors    message    reason    status    timestamp
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError constraintViolationException(final ConstraintViolationException e) {
        log.error("Ошибка при попытке добавления записи в БД ", e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Значения не должны совпадать.",
                HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError dataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("Ошибка при попытке добавления записи в БД ", e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Значения не должны совпадать.",
                HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflictException(final ConflictException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Значения не должны совпадать.",
                HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadrequestException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Переданные значение не прошли валидацию",
                HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "handleNotFoundException",
                HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectParameterException(final ValidationException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "handleNotFoundException", HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {

        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            errors.add("Переменная  " + fieldError.getField() +
                    "Ошибка  " + fieldError.getDefaultMessage() +
                    "Значение  " + fieldError.getRejectedValue());
        }
        return new ApiError(
                errors, "Ошибка валидации.",
                e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError propertyValueException(final PropertyValueException e) {
        log.error(e.getMessage());
        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Ошибки в запросе.",
                HttpStatus.CONFLICT, LocalDateTime.now());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError otherErrors(final RuntimeException e) {
//        log.error(e.getMessage());
//        return new ApiError(List.of(e.getMessage()), e.getMessage(), "Ошибки в запросе",
//                HttpStatus.BAD_REQUEST, LocalDateTime.now());
//    }
}