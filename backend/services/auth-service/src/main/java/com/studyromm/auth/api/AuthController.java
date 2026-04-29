package com.studyromm.auth.api;

import com.studyromm.auth.application.LoginService;
import com.studyromm.auth.application.ResourceAccessAuditService;
import com.studyromm.auth.application.AuditLogService;
import com.studyromm.auth.application.AuthorizationSummary;
import com.studyromm.auth.application.AuthorizationSummaryService;
import com.studyromm.platform.security.AuthenticatedActor;
import com.studyromm.platform.security.SecurityContextActors;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final LoginService loginService;
    private final AuthorizationSummaryService authorizationSummaryService;
    private final ResourceAccessAuditService resourceAccessAuditService;
    private final AuditLogService auditLogService;

    public AuthController(
            LoginService loginService,
            AuthorizationSummaryService authorizationSummaryService,
            ResourceAccessAuditService resourceAccessAuditService,
            AuditLogService auditLogService
    ) {
        this.loginService = loginService;
        this.authorizationSummaryService = authorizationSummaryService;
        this.resourceAccessAuditService = resourceAccessAuditService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/api/auth/capabilities")
    public Map<String, Object> capabilities() {
        return Map.of(
                "service", "auth-service",
                "status", "DEV_003_BOOTSTRAP_READY",
                "features", new String[]{"authentication", "authorization", "service-identity", "audit"}
        );
    }

    @PostMapping("/api/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            throw new BadCredentialsException("invalid username or password");
        }

        LoginService.AuthenticatedLogin authenticatedLogin =
                loginService.authenticate(request.username(), request.password());
        return new LoginResponse(
                "Bearer",
                authenticatedLogin.issuedToken().token(),
                authenticatedLogin.issuedToken().expiresAt(),
                authenticatedLogin.user().userId(),
                authenticatedLogin.user().username(),
                authenticatedLogin.user().displayName(),
                authenticatedLogin.user().roleCodes()
        );
    }

    @GetMapping("/api/common/me")
    public CurrentActorResponse currentActor() {
        AuthenticatedActor actor = SecurityContextActors.getCurrentActor();
        AuthorizationSummary summary = authorizationSummaryService.buildSummary(actor);
        return new CurrentActorResponse(
                actor.actorType().name(),
                actor.actorId(),
                actor.username(),
                actor.displayName(),
                actor.serviceName(),
                actor.roleCodes(),
                new AuthorizationSummaryResponse(
                        summary.platformAdmin(),
                        summary.classRoomIds(),
                        summary.membershipRoles(),
                        summary.teachingSubjectCodes(),
                        summary.teachingAssignmentClassRoomIds(),
                        summary.ownedResourceTypes()
                )
        );
    }

    @GetMapping("/api/auth/access-check")
    public ResourceAccessResponse accessCheck(String resourceType, String resourceId) {
        AuthenticatedActor actor = SecurityContextActors.getCurrentActor();
        resourceAccessAuditService.verifyAccessOrThrow(actor, resourceType, resourceId);
        return new ResourceAccessResponse(resourceType, resourceId, true);
    }

    @PostMapping("/api/auth/logout")
    public java.util.Map<String, Object> logout() {
        AuthenticatedActor actor = SecurityContextActors.getCurrentActor();
        auditLogService.appendLogout(actor);
        return java.util.Map.of("success", true);
    }
}
