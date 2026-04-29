package com.studyromm.platform.security;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "studyromm.security")
public class StudyRoomSecurityProperties {

    private String tokenSecret = "dev-only-token-secret-change-me";
    private Duration tokenTtl = Duration.ofHours(8);
    private final Map<String, String> serviceTokens = new LinkedHashMap<>();

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public Duration getTokenTtl() {
        return tokenTtl;
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

    public Map<String, String> getServiceTokens() {
        return serviceTokens;
    }
}
