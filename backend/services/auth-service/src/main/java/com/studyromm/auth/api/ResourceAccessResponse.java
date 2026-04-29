package com.studyromm.auth.api;

public record ResourceAccessResponse(
        String resourceType,
        String resourceId,
        boolean allowed
) {
}
