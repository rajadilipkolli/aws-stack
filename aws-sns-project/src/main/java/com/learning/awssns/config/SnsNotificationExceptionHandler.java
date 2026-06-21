package com.learning.awssns.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.JsonNodeException;

/**
 * Global exception handler for SNS notification processing errors
 */
@RestControllerAdvice
public class SnsNotificationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SnsNotificationExceptionHandler.class);

    /**
     * Handle JsonNodeException that occurs when required SNS message fields are missing
     *
     * @return 204 No Content to acknowledge receipt without processing invalid data
     */
    @ExceptionHandler(JsonNodeException.class)
    public ProblemDetail handleJsonNodeException(JsonNodeException ex) {
        log.warn("JsonNodeException while processing SNS notification: {}", ex.getMessage());

        // Return 204 No Content instead of 500 error when fields are missing
        // This allows the notification to be acknowledged without storing invalid data
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Missing notification fields");
        problemDetail.setDetail("Required notification fields (Message or Subject) are missing: " + ex.getMessage());

        return problemDetail;
    }
}
