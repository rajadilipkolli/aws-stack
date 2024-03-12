package com.learning.awspring.config;

import com.learning.awspring.exception.BucketNotFoundException;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onException(MethodArgumentNotValidException methodArgumentNotValidException) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatusCode.valueOf(400), "Invalid request content.");
        problemDetail.setTitle("Constraint Violation");
        List<ApiValidationError> validationErrorsList =
                methodArgumentNotValidException.getAllErrors().stream()
                        .map(
                                objectError -> {
                                    FieldError fieldError = (FieldError) objectError;
                                    return new ApiValidationError(
                                            fieldError.getObjectName(),
                                            fieldError.getField(),
                                            fieldError.getRejectedValue(),
                                            Objects.requireNonNull(
                                                    fieldError.getDefaultMessage(), ""));
                                })
                        .sorted(Comparator.comparing(ApiValidationError::field))
                        .toList();
        problemDetail.setProperty("violations", validationErrorsList);
        return problemDetail;
    }

    @ExceptionHandler(BucketNotFoundException.class)
    ProblemDetail onException(BucketNotFoundException bucketNotFoundException) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatusCode.valueOf(404), bucketNotFoundException.getMessage());
        problemDetail.setTitle("S3 Bucket NotFound");
        return problemDetail;
    }

    @ExceptionHandler(FileNotFoundException.class)
    ProblemDetail onFileNotFoundException(FileNotFoundException fileNotFoundException) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatusCode.valueOf(404), fileNotFoundException.getMessage());
        problemDetail.setTitle("File NotFound");
        return problemDetail;
    }

    record ApiValidationError(String object, String field, Object rejectedValue, String message) {}
}
