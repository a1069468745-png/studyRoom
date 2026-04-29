package com.studyromm.auth.api;

public record LoginRequest(
        String username,
        String password
) {
}
