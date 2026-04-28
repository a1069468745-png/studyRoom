package com.studyromm.knowledge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studyromm.platform.testing.TraceIdAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class KnowledgeServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsServiceNameAndTraceId() throws Exception {
        mockMvc.perform(get("/api/common/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.service").value("knowledge-service"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void commonTaskEndpointReturnsBootstrapContract() throws Exception {
        mockMvc.perform(get("/api/common/tasks/job_knowledge_bootstrap"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.taskId").value("job_knowledge_bootstrap"))
                .andExpect(jsonPath("$.taskType").value("BOOTSTRAP"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.progress").value(0))
                .andExpect(jsonPath("$.retryable").value(true))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void unknownRouteReturnsUnifiedErrorStructure() throws Exception {
        mockMvc.perform(get("/api/knowledge/missing"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }
}
