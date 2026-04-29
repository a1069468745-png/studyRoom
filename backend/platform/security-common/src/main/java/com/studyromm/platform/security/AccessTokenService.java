package com.studyromm.platform.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final StudyRoomSecurityProperties properties;

    public AccessTokenService(StudyRoomSecurityProperties properties) {
        this.properties = properties;
    }

    public IssuedToken issueToken(
            String userId,
            String username,
            String displayName,
            List<String> roleCodes
    ) {
        Instant expiresAt = Instant.now().plus(properties.getTokenTtl());
        String payload = String.join(
                "|",
                sanitize(userId),
                sanitize(username),
                sanitize(displayName),
                sanitize(String.join(",", roleCodes)),
                String.valueOf(expiresAt.toEpochMilli())
        );
        return new IssuedToken(encode(payload) + "." + sign(payload), expiresAt);
    }

    public AuthenticatedActor parseUserToken(String token) {
        try {
            String[] segments = token.split("\\.", 2);
            if (segments.length != 2) {
                throw new BadCredentialsException("invalid access token");
            }

            String payload = decode(segments[0]);
            if (!sign(payload).equals(segments[1])) {
                throw new BadCredentialsException("invalid access token");
            }

            String[] fields = payload.split("\\|", -1);
            if (fields.length != 5) {
                throw new BadCredentialsException("invalid access token");
            }

            Instant expiresAt = Instant.ofEpochMilli(Long.parseLong(fields[4]));
            if (expiresAt.isBefore(Instant.now())) {
                throw new BadCredentialsException("access token expired");
            }

            return new AuthenticatedActor(
                    AuthenticatedActor.ActorType.USER,
                    fields[0],
                    fields[1],
                    fields[2],
                    null,
                    fields[3].isBlank() ? List.of() : List.of(fields[3].split(","))
            );
        } catch (BadCredentialsException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BadCredentialsException("invalid access token", exception);
        }
    }

    public AuthenticatedActor parseServiceActor(String serviceName, String serviceToken) {
        String expectedToken = properties.getServiceTokens().get(serviceName);
        if (expectedToken == null || !expectedToken.equals(serviceToken)) {
            throw new BadCredentialsException("invalid service identity");
        }

        return new AuthenticatedActor(
                AuthenticatedActor.ActorType.SERVICE,
                serviceName,
                serviceName,
                serviceName,
                serviceName,
                List.of("SERVICE")
        );
    }

    public record IssuedToken(String token, Instant expiresAt) {
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", " ");
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(
                    properties.getTokenSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            ));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    mac.doFinal(payload.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception exception) {
            throw new IllegalStateException("failed to sign access token", exception);
        }
    }

    private String encode(String payload) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String payload) {
        return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    }
}
