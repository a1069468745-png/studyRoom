package com.studyromm.gateway.api;

import com.studyromm.platform.service.health.HealthResponse;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayHealthController {

    private final String serviceName;

    public GatewayHealthController(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    @GetMapping("/api/common/health")
    public HealthResponse health() {
        return new HealthResponse(serviceName, "UP", Instant.now());
    }
}
