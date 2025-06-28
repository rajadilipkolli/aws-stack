package com.learning.awspring.config;

import com.learning.awspring.exception.BucketNotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BucketNotFoundException.class)
    ProblemDetail handleBucketNotFoundException(BucketNotFoundException ex) {
        log.error("Bucket not found: {}", ex.getMessage());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Bucket Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(
                URI.create("https://api.s3project.example.com/errors/bucket-not-found"));
        return problemDetail;
    }

    @ExceptionHandler(FileNotFoundException.class)
    ProblemDetail handleFileNotFoundException(FileNotFoundException ex) {
        log.error("File not found: {}", ex.getMessage());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("File Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(
                URI.create("https://api.s3project.example.com/errors/file-not-found"));
        return problemDetail;
    }

    @ExceptionHandler(S3Exception.class)
    ProblemDetail handleS3Exception(S3Exception ex) {
        log.error("AWS S3 exception: {}", ex.getMessage());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("S3 Service Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("awsErrorCode", ex.awsErrorDetails().errorCode());
        problemDetail.setProperty("errorMessage", ex.awsErrorDetails().errorMessage());
        problemDetail.setType(URI.create("https://api.s3project.example.com/errors/s3-error"));
        return problemDetail;
    }

    @ExceptionHandler(SdkClientException.class)
    ProblemDetail handleSdkClientException(SdkClientException ex) {
        log.error("AWS SDK client exception: {}", ex.getMessage());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problemDetail.setTitle("AWS Client Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(
                URI.create("https://api.s3project.example.com/errors/aws-client-error"));
        return problemDetail;
    }

    @ExceptionHandler(IOException.class)
    ProblemDetail handleIOException(IOException ex) {
        log.error("IO exception: {}", ex.getMessage());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("I/O Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(URI.create("https://api.s3project.example.com/errors/io-error"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleGeneralException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(URI.create("https://api.s3project.example.com/errors/general-error"));
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        log.error("Validation error: {}", ex.getMessage());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "Invalid request content.");
        problemDetail.setTitle("Constraint Violation");

        List<ApiValidationError> validationErrorsList =
                ex.getAllErrors().stream()
                        .map(
                                objectError -> {
                                    if (objectError instanceof FieldError fieldError) {
                                        return new ApiValidationError(
                                                fieldError.getObjectName(),
                                                fieldError.getField(),
                                                fieldError.getRejectedValue(),
                                                Objects.requireNonNull(
                                                        fieldError.getDefaultMessage(), ""));
                                    }
                                    // Handle non-field errors if needed
                                    return new ApiValidationError(
                                            objectError.getObjectName(),
                                            "",
                                            null,
                                            Objects.requireNonNull(
                                                    objectError.getDefaultMessage(), ""));
                                })
                        .sorted(Comparator.comparing(ApiValidationError::field))
                        .toList();

        problemDetail.setProperty("violations", validationErrorsList);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(
                URI.create("https://api.s3project.example.com/errors/validation-error"));

        return new ResponseEntity<>(problemDetail, headers, status);
    }

    record ApiValidationError(String object, String field, Object rejectedValue, String message) {}
}
