package com.studyromm.auth.api;

import com.studyromm.platform.web.error.ApiErrorResponse;
import com.studyromm.platform.web.trace.TraceIdHolder;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
                "AUTH_UNAUTHORIZED",
                exception.getMessage() == null ? "authentication required" : exception.getMessage(),
                TraceIdHolder.getTraceId(),
                Instant.now()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiErrorResponse(
                "AUTH_FORBIDDEN",
                exception.getMessage() == null ? "forbidden" : exception.getMessage(),
                TraceIdHolder.getTraceId(),
                Instant.now()
        ));
    }
}
