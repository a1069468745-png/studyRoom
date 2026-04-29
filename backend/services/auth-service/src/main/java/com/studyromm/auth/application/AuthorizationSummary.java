package com.studyromm.auth.application;

import java.util.List;

public record AuthorizationSummary(
        boolean platformAdmin,
        List<String> classRoomIds,
        List<String> membershipRoles,
        List<String> teachingSubjectCodes,
        List<String> teachingAssignmentClassRoomIds,
        List<String> ownedResourceTypes
) {
}
