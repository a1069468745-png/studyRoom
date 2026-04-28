package com.studyromm.auth.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/capabilities")
    public Map<String, Object> capabilities() {
        return Map.of(
                "service", "auth-service",
                "status", "BOOTSTRAP_READY",
                "features", new String[]{"authentication", "authorization"}
        );
    }
}
