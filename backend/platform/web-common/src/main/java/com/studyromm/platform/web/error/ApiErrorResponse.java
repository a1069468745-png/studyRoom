package com.studyromm.platform.web.error;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        String traceId,
        Instant timestamp
) {
}
