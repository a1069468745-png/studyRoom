package com.studyromm.platform.web.health;

import com.studyromm.platform.service.health.HealthResponse;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final String serviceName;

    public HealthController(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    @GetMapping("/api/common/health")
    public HealthResponse health() {
        return new HealthResponse(serviceName, "UP", Instant.now());
    }
}
