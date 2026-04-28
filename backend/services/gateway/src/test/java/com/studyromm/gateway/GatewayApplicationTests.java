package com.studyromm.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.studyromm.gateway.api.GatewayHealthController;
import com.studyromm.gateway.api.GatewayTaskController;
import com.studyromm.gateway.trace.TraceIdWebFilter;

@WebFluxTest(controllers = {
        GatewayHealthController.class,
        GatewayTaskController.class
})
@Import(TraceIdWebFilter.class)
@TestPropertySource(properties = "spring.application.name=gateway")
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void healthEndpointReturnsServiceNameAndTraceId() {
        webTestClient.get()
                .uri("/api/common/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Trace-Id")
                .expectBody()
                .jsonPath("$.service").isEqualTo("gateway")
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void commonTaskEndpointReturnsBootstrapContract() {
        webTestClient.get()
                .uri("/api/common/tasks/job_gateway_bootstrap")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Trace-Id")
                .expectBody()
                .jsonPath("$.taskId").isEqualTo("job_gateway_bootstrap")
                .jsonPath("$.taskType").isEqualTo("BOOTSTRAP")
                .jsonPath("$.status").isEqualTo("PENDING")
                .jsonPath("$.progress").isEqualTo(0)
                .jsonPath("$.retryable").isEqualTo(true)
                .jsonPath("$.traceId").isNotEmpty();
    }
}
