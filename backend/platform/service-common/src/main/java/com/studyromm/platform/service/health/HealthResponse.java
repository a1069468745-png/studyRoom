package com.studyromm.platform.service.health;

import java.time.Instant;

public record HealthResponse(
        String service,
        String status,
        Instant timestamp
) {
}
