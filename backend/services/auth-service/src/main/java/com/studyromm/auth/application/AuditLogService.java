package com.studyromm.auth.application;

import com.studyromm.platform.security.AuthenticatedActor;
import com.studyromm.platform.web.trace.TraceIdHolder;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void append(
            String id,
            AuthenticatedActor actor,
            String actionCode,
            String resourceType,
            String resourceId,
            String resultCode,
            String detailJson
    ) {
        jdbcTemplate.update(
                """
                        insert into auth_service.audit_log
                        (id, trace_id, actor_user_id, actor_role_code, action_code, resource_type, resource_id, result_code, detail_json, created_by)
                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                id,
                resolveTraceId(),
                actor != null && actor.actorType() == AuthenticatedActor.ActorType.USER ? actor.actorId() : null,
                firstRole(actor),
                actionCode,
                resourceType,
                resourceId,
                resultCode,
                detailJson,
                actor == null ? null : actor.actorId()
        );
    }

    public void appendAction(
            AuthenticatedActor actor,
            AuditActionCode actionCode,
            String resourceType,
            String resourceId,
            AuditResultCode resultCode,
            String detailJson
    ) {
        append(
                actionCode.name().toLowerCase() + "-" + resourceId + "-" + System.nanoTime(),
                actor,
                actionCode.name(),
                resourceType,
                resourceId,
                resultCode.name(),
                detailJson
        );
    }

    public void appendLoginSuccess(AuthUser user) {
        append(
                "login-success-" + user.userId() + "-" + System.nanoTime(),
                new AuthenticatedActor(
                        AuthenticatedActor.ActorType.USER,
                        user.userId(),
                        user.username(),
                        user.displayName(),
                        null,
                        user.roleCodes()
                ),
                "LOGIN",
                "AUTH_SESSION",
                user.userId(),
                "SUCCESS",
                "{\"username\":\"" + user.username() + "\"}"
        );
    }

    public void appendLoginFailure(String username) {
        append(
                "login-failure-" + username + "-" + System.nanoTime(),
                null,
                "LOGIN",
                "AUTH_SESSION",
                username,
                "FAILED",
                "{\"username\":\"" + username + "\"}"
        );
    }

    public void appendLogout(AuthenticatedActor actor) {
        appendAction(
                actor,
                AuditActionCode.LOGOUT,
                "AUTH_SESSION",
                actor.actorId(),
                AuditResultCode.SUCCESS,
                "{\"username\":\"" + actor.username() + "\"}"
        );
    }

    private String firstRole(AuthenticatedActor actor) {
        if (actor == null || actor.roleCodes().isEmpty()) {
            return null;
        }
        return actor.roleCodes().get(0);
    }

    private String resolveTraceId() {
        String traceId = TraceIdHolder.getTraceId();
        if (traceId == null || traceId.isBlank()) {
            return "trace-" + UUID.randomUUID();
        }
        return traceId;
    }
}
