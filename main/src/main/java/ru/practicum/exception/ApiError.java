package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class ApiError {
    List<String> errors;
    String message;
    String reason;
    HttpStatus status;
    LocalDateTime timestamp;
}
