package com.studyromm.auth.api;

import java.time.Instant;
import java.util.List;

public record LoginResponse(
        String tokenType,
        String accessToken,
        Instant expiresAt,
        String userId,
        String username,
        String displayName,
        List<String> roleCodes
) {
}
