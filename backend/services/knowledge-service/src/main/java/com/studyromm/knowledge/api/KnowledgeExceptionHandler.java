package com.studyromm.knowledge.api;

import com.studyromm.platform.web.error.ApiErrorResponse;
import com.studyromm.platform.web.trace.TraceIdHolder;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KnowledgeExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ApiErrorResponse(
                "VALIDATION_FAILED",
                ex.getMessage(),
                TraceIdHolder.getTraceId(),
                Instant.now()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleIllegalState(IllegalStateException ex) {
        return new ApiErrorResponse(
                "BUSINESS_RULE_VIOLATION",
                ex.getMessage(),
                TraceIdHolder.getTraceId(),
                Instant.now()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(NoSuchElementException ex) {
        return new ApiErrorResponse(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                TraceIdHolder.getTraceId(),
                Instant.now()
        );
    }
}
