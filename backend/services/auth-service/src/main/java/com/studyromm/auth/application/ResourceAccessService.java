package com.studyromm.auth.application;

import com.studyromm.platform.security.AuthenticatedActor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResourceAccessService {

    private final JdbcTemplate jdbcTemplate;

    public ResourceAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean canAccessResource(AuthenticatedActor actor, String resourceType, String resourceId) {
        if (actor == null) {
            return false;
        }
        if (actor.actorType() == AuthenticatedActor.ActorType.SERVICE) {
            return true;
        }
        if (actor.roleCodes().contains("ADMIN")) {
            return true;
        }

        Integer directOwnerCount = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from auth_service.resource_owner_scope
                        where resource_type = ?
                          and resource_id = ?
                          and owner_user_id = ?
                          and status = 'ACTIVE'
                          and is_deleted = false
                        """,
                Integer.class,
                resourceType,
                resourceId,
                actor.actorId()
        );
        if (directOwnerCount != null && directOwnerCount > 0) {
            return true;
        }

        Integer classScopeCount = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from auth_service.resource_owner_scope ros
                        join auth_service.class_membership cm
                          on cm.class_room_id = ros.owner_scope_ref
                         and cm.user_id = ?
                         and cm.status = 'ACTIVE'
                         and cm.is_deleted = false
                        where ros.resource_type = ?
                          and ros.resource_id = ?
                          and ros.owner_scope_type = 'CLASS_ROOM'
                          and ros.status = 'ACTIVE'
                          and ros.is_deleted = false
                        """,
                Integer.class,
                actor.actorId(),
                resourceType,
                resourceId
        );
        if (classScopeCount != null && classScopeCount > 0) {
            return true;
        }

        Integer teachingScopeCount = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from auth_service.resource_owner_scope ros
                        join auth_service.teaching_assignment ta
                          on ta.class_room_id = ros.owner_scope_ref
                         and ta.teacher_user_id = ?
                         and ta.status = 'ACTIVE'
                         and ta.is_deleted = false
                        where ros.resource_type = ?
                          and ros.resource_id = ?
                          and ros.owner_scope_type = 'CLASS_ROOM'
                          and ros.status = 'ACTIVE'
                          and ros.is_deleted = false
                        """,
                Integer.class,
                actor.actorId(),
                resourceType,
                resourceId
        );
        return teachingScopeCount != null && teachingScopeCount > 0;
    }
}
