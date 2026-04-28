package com.studyromm.platform.web.error;

import com.studyromm.platform.web.trace.TraceIdHolder;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                "NOT_FOUND",
                exception.getHttpMethod() + " " + exception.getRequestURL() + " not found",
                TraceIdHolder.getTraceId(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                "INTERNAL_ERROR",
                exception.getMessage() == null ? "unexpected error" : exception.getMessage(),
                TraceIdHolder.getTraceId(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
