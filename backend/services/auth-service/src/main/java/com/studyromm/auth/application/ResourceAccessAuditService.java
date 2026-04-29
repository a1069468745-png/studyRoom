package com.studyromm.auth.application;

import com.studyromm.platform.security.AuthenticatedActor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ResourceAccessAuditService {

    private final ResourceAccessService resourceAccessService;
    private final AuditLogService auditLogService;

    public ResourceAccessAuditService(
            ResourceAccessService resourceAccessService,
            AuditLogService auditLogService
    ) {
        this.resourceAccessService = resourceAccessService;
        this.auditLogService = auditLogService;
    }

    public boolean verifyAccessOrThrow(AuthenticatedActor actor, String resourceType, String resourceId) {
        boolean allowed = resourceAccessService.canAccessResource(actor, resourceType, resourceId);
        auditLogService.appendAction(
                actor,
                AuditActionCode.RESOURCE_ACCESS_CHECK,
                resourceType,
                resourceId,
                allowed ? AuditResultCode.SUCCESS : AuditResultCode.FORBIDDEN,
                "{\"allowed\":" + allowed + "}"
        );
        if (!allowed) {
            throw new AccessDeniedException("resource access denied");
        }
        return true;
    }
}
