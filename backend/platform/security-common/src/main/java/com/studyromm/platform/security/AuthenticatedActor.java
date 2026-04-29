package com.studyromm.platform.security;

import java.util.List;

public record AuthenticatedActor(
        ActorType actorType,
        String actorId,
        String username,
        String displayName,
        String serviceName,
        List<String> roleCodes
) {

    public enum ActorType {
        USER,
        SERVICE
    }
}
