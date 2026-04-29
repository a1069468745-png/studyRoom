package com.studyromm.auth.application;

import com.studyromm.platform.security.AuthenticatedActor;
import org.springframework.stereotype.Service;

@Service
public class BusinessAuditBaselineService {

    private final AuditLogService auditLogService;

    public BusinessAuditBaselineService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    public void recordReview(
            AuthenticatedActor actor,
            String resourceType,
            String resourceId,
            boolean success
    ) {
        record(actor, AuditActionCode.REVIEW, resourceType, resourceId, success);
    }

    public void recordGrading(
            AuthenticatedActor actor,
            String resourceType,
            String resourceId,
            boolean success
    ) {
        record(actor, AuditActionCode.GRADING, resourceType, resourceId, success);
    }

    public void recordPublish(
            AuthenticatedActor actor,
            String resourceType,
            String resourceId,
            boolean success
    ) {
        record(actor, AuditActionCode.PUBLISH, resourceType, resourceId, success);
    }

    public void recordDelete(
            AuthenticatedActor actor,
            String resourceType,
            String resourceId,
            boolean success
    ) {
        record(actor, AuditActionCode.DELETE, resourceType, resourceId, success);
    }

    public void recordExport(
            AuthenticatedActor actor,
            String resourceType,
            String resourceId,
            boolean success
    ) {
        record(actor, AuditActionCode.EXPORT, resourceType, resourceId, success);
    }

    private void record(
            AuthenticatedActor actor,
            AuditActionCode actionCode,
            String resourceType,
            String resourceId,
            boolean success
    ) {
        auditLogService.appendAction(
                actor,
                actionCode,
                resourceType,
                resourceId,
                success ? AuditResultCode.SUCCESS : AuditResultCode.FAILED,
                "{\"baseline\":true}"
        );
    }
}
