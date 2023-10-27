package ru.practicum.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.*;
import ru.practicum.exceptions.dto.ApiError;
import org.hibernate.exception.ConstraintViolationException;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(final UserNotFoundException e) {
        log.warn("UserNotFoundException!, {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRequestNotFoundException(final RequestNotFoundException e) {
        log.warn("RequestNotFoundException!, {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEventNotFoundException(final EventNotFoundException e) {
        log.warn("EventNotFoundException!, {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompilationNotFoundException(final CompilationNotFoundException e) {
        log.warn("CompilationNotFoundException!, {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFoundException(final CategoryNotFoundException e) {
        log.warn("CategoryNotFoundException!, {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleLocationNotFoundException(final LocationNotFoundException e) {
        log.warn("CategoryNotFoundException!, {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn("ValidationException!, {}", e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationRequestException(final ValidationRequestException e) {
        log.warn("ValidationException!, {}", e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException!, {}", e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("ConstraintViolationException!, {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenArgumentException(final ForbiddenArgumentException e) {
        log.warn("ForbiddenArgumentException!, {}", e.getMessage());
        return new ApiError(HttpStatus.FORBIDDEN, "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now());
    }
}
