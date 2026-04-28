package com.studyromm.knowledge.api;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeBootstrapController {

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap() {
        return Map.of(
                "service", "knowledge-service",
                "status", "BOOTSTRAP_READY",
                "scopes", List.of("curriculum-tree", "knowledge-points", "content-assets")
        );
    }
}
