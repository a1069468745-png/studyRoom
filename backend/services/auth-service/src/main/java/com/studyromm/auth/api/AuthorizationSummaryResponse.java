package com.studyromm.auth.api;

import java.util.List;

public record AuthorizationSummaryResponse(
        boolean platformAdmin,
        List<String> classRoomIds,
        List<String> membershipRoles,
        List<String> teachingSubjectCodes,
        List<String> teachingAssignmentClassRoomIds,
        List<String> ownedResourceTypes
) {
}
