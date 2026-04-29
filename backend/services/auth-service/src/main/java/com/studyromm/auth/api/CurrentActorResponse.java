package com.studyromm.auth.api;

import java.util.List;

public record CurrentActorResponse(
        String actorType,
        String actorId,
        String username,
        String displayName,
        String serviceName,
        List<String> roleCodes,
        AuthorizationSummaryResponse authorizationSummary
) {
}
