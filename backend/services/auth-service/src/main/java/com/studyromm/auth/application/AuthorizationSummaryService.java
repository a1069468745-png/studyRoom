package com.studyromm.auth.application;

import com.studyromm.platform.security.AuthenticatedActor;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationSummaryService {

    private final JdbcTemplate jdbcTemplate;

    public AuthorizationSummaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthorizationSummary buildSummary(AuthenticatedActor actor) {
        if (actor == null) {
            return emptySummary(false);
        }
        if (actor.actorType() == AuthenticatedActor.ActorType.SERVICE) {
            return emptySummary(false);
        }

        boolean platformAdmin = actor.roleCodes().contains("ADMIN");
        Set<String> classRoomIds = new LinkedHashSet<>();
        Set<String> membershipRoles = new LinkedHashSet<>();
        Set<String> teachingSubjectCodes = new LinkedHashSet<>();
        Set<String> teachingAssignmentClassRoomIds = new LinkedHashSet<>();
        Set<String> ownedResourceTypes = new LinkedHashSet<>();

        List<Map<String, Object>> memberships = jdbcTemplate.queryForList(
                """
                        select class_room_id, membership_role
                        from auth_service.class_membership
                        where user_id = ?
                          and status = 'ACTIVE'
                          and is_deleted = false
                        """,
                actor.actorId()
        );
        for (Map<String, Object> membership : memberships) {
            classRoomIds.add(membership.get("class_room_id").toString());
            membershipRoles.add(membership.get("membership_role").toString());
        }

        List<Map<String, Object>> assignments = jdbcTemplate.queryForList(
                """
                        select class_room_id, subject_code
                        from auth_service.teaching_assignment
                        where teacher_user_id = ?
                          and status = 'ACTIVE'
                          and is_deleted = false
                        """,
                actor.actorId()
        );
        for (Map<String, Object> assignment : assignments) {
            teachingAssignmentClassRoomIds.add(assignment.get("class_room_id").toString());
            teachingSubjectCodes.add(assignment.get("subject_code").toString());
        }

        List<Map<String, Object>> ownerScopes = jdbcTemplate.queryForList(
                """
                        select distinct resource_type
                        from auth_service.resource_owner_scope
                        where owner_user_id = ?
                          and status = 'ACTIVE'
                          and is_deleted = false
                        """,
                actor.actorId()
        );
        for (Map<String, Object> ownerScope : ownerScopes) {
            ownedResourceTypes.add(ownerScope.get("resource_type").toString());
        }

        return new AuthorizationSummary(
                platformAdmin,
                List.copyOf(classRoomIds),
                List.copyOf(membershipRoles),
                List.copyOf(teachingSubjectCodes),
                List.copyOf(teachingAssignmentClassRoomIds),
                List.copyOf(ownedResourceTypes)
        );
    }

    private AuthorizationSummary emptySummary(boolean platformAdmin) {
        return new AuthorizationSummary(
                platformAdmin,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }
}
