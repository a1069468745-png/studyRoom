package com.studyromm.auth.application;

import java.util.List;

public record AuthUser(
        String userId,
        String username,
        String displayName,
        String passwordHash,
        List<String> roleCodes
) {
}
