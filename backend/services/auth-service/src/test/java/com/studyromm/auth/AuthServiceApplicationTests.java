package com.studyromm.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studyromm.auth.application.ResourceAccessService;
import com.studyromm.auth.application.BusinessAuditBaselineService;
import com.studyromm.platform.security.AuthenticatedActor;
import com.studyromm.platform.testing.TraceIdAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceAccessService resourceAccessService;

    @Autowired
    private BusinessAuditBaselineService businessAuditBaselineService;

    @Test
    void healthEndpointReturnsServiceNameAndTraceId() throws Exception {
        mockMvc.perform(get("/api/common/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.service").value("auth-service"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void commonTaskEndpointReturnsBootstrapContract() throws Exception {
        mockMvc.perform(get("/api/common/tasks/job_123"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.taskId").value("job_123"))
                .andExpect(jsonPath("$.taskType").value("BOOTSTRAP"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.progress").value(0))
                .andExpect(jsonPath("$.retryable").value(true))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void unknownRouteReturnsUnifiedErrorStructure() throws Exception {
        mockMvc.perform(get("/api/auth/missing"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void meEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/common/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    void loginReturnsBearerTokenAndCurrentActor() throws Exception {
        seedUser("teacher-2", "teacher.two", "Teacher Two", "pass-123", "TEACHER");
        seedTeachingAssignment("assignment-1", "teacher-2", "class-2", "MATH", "textbook-1");
        seedClassMembership("membership-2", "class-2", "teacher-2", "TEACHER");
        seedOwnerScope("scope-2", "QUESTION", "question-2", "teacher-2", "CLASS_ROOM", "class-2");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"teacher.two","password":"pass-123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.roleCodes[0]").value("TEACHER"))
                .andReturn();

        String token = extractJsonValue(loginResult.getResponse().getContentAsString(), "accessToken");

        mockMvc.perform(get("/api/common/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorType").value("USER"))
                .andExpect(jsonPath("$.actorId").value("teacher-2"))
                .andExpect(jsonPath("$.username").value("teacher.two"))
                .andExpect(jsonPath("$.roleCodes[0]").value("TEACHER"))
                .andExpect(jsonPath("$.authorizationSummary.platformAdmin").value(false))
                .andExpect(jsonPath("$.authorizationSummary.classRoomIds[0]").value("class-2"))
                .andExpect(jsonPath("$.authorizationSummary.membershipRoles[0]").value("TEACHER"))
                .andExpect(jsonPath("$.authorizationSummary.teachingSubjectCodes[0]").value("MATH"))
                .andExpect(jsonPath("$.authorizationSummary.teachingAssignmentClassRoomIds[0]").value("class-2"))
                .andExpect(jsonPath("$.authorizationSummary.ownedResourceTypes[0]").value("QUESTION"));

        Integer auditCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.audit_log where action_code = 'LOGIN' and result_code = 'SUCCESS' and actor_user_id = ?",
                Integer.class,
                "teacher-2"
        );
        Assertions.assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void loginFailureReturnsUnauthorizedAndAuditLog() throws Exception {
        seedUser("teacher-3", "teacher.three", "Teacher Three", "pass-123", "TEACHER");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"teacher.three","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));

        Integer auditCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.audit_log where action_code = 'LOGIN' and result_code = 'FAILED' and resource_id = ?",
                Integer.class,
                "teacher.three"
        );
        Assertions.assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void logoutReturnsSuccessAndWritesAuditLog() throws Exception {
        seedUser("teacher-logout", "teacher.logout", "Teacher Logout", "pass-123", "TEACHER");
        String token = loginAndReturnToken("teacher.logout", "pass-123");

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Integer auditCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.audit_log where action_code = 'LOGOUT' and result_code = 'SUCCESS' and actor_user_id = ?",
                Integer.class,
                "teacher-logout"
        );
        Assertions.assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void businessAuditBaselineWritesAllActionTypes() {
        seedUser("teacher-audit", "teacher.audit", "Teacher Audit", "pass-123", "TEACHER");
        AuthenticatedActor actor = new AuthenticatedActor(
                AuthenticatedActor.ActorType.USER,
                "teacher-audit",
                "teacher.audit",
                "Teacher Audit",
                null,
                java.util.List.of("TEACHER")
        );

        businessAuditBaselineService.recordReview(actor, "QUESTION", "q-audit-1", true);
        businessAuditBaselineService.recordGrading(actor, "SUBMISSION", "s-audit-1", true);
        businessAuditBaselineService.recordPublish(actor, "VIDEO_DRAFT", "v-audit-1", true);
        businessAuditBaselineService.recordDelete(actor, "QUESTION", "q-audit-2", false);
        businessAuditBaselineService.recordExport(actor, "ANALYSIS_REPORT", "r-audit-1", true);

        assertActionCount("REVIEW", "SUCCESS", "q-audit-1", 1);
        assertActionCount("GRADING", "SUCCESS", "s-audit-1", 1);
        assertActionCount("PUBLISH", "SUCCESS", "v-audit-1", 1);
        assertActionCount("DELETE", "FAILED", "q-audit-2", 1);
        assertActionCount("EXPORT", "SUCCESS", "r-audit-1", 1);
    }

    @Test
    void serviceIdentityCanAccessCurrentActorEndpoint() throws Exception {
        mockMvc.perform(get("/api/common/me")
                        .header("X-Service-Name", "knowledge-service")
                        .header("X-Service-Token", "knowledge-service-dev-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorType").value("SERVICE"))
                .andExpect(jsonPath("$.serviceName").value("knowledge-service"))
                .andExpect(jsonPath("$.roleCodes[0]").value("SERVICE"))
                .andExpect(jsonPath("$.authorizationSummary.platformAdmin").value(false))
                .andExpect(jsonPath("$.authorizationSummary.classRoomIds").isEmpty());
    }

    @Test
    void resourceAccessServiceHonorsAdminDirectAndClassScopes() {
        seedUser("teacher-4", "teacher.four", "Teacher Four", "pass-123", "TEACHER");
        seedClassMembership("membership-4", "class-4", "teacher-4", "TEACHER");
        seedTeachingAssignment("assignment-4", "teacher-4", "class-4", "ENGLISH", "textbook-1");
        seedOwnerScope("scope-4", "QUESTION", "question-4", null, "CLASS_ROOM", "class-4");

        AuthenticatedActor teacherActor = new AuthenticatedActor(
                AuthenticatedActor.ActorType.USER,
                "teacher-4",
                "teacher.four",
                "Teacher Four",
                null,
                java.util.List.of("TEACHER")
        );
        Assertions.assertThat(resourceAccessService.canAccessResource(teacherActor, "QUESTION", "question-4"))
                .isTrue();

        seedUser("student-4", "student.four", "Student Four", "pass-123", "STUDENT");
        seedClassMembership("membership-5", "class-4", "student-4", "STUDENT");
        AuthenticatedActor studentActor = new AuthenticatedActor(
                AuthenticatedActor.ActorType.USER,
                "student-4",
                "student.four",
                "Student Four",
                null,
                java.util.List.of("STUDENT")
        );
        Assertions.assertThat(resourceAccessService.canAccessResource(studentActor, "QUESTION", "question-4"))
                .isTrue();

        seedUser("admin-1", "admin.one", "Admin One", "pass-123", "ADMIN");
        AuthenticatedActor adminActor = new AuthenticatedActor(
                AuthenticatedActor.ActorType.USER,
                "admin-1",
                "admin.one",
                "Admin One",
                null,
                java.util.List.of("ADMIN")
        );
        Assertions.assertThat(resourceAccessService.canAccessResource(adminActor, "QUESTION", "question-4"))
                .isTrue();

        seedUser("teacher-5", "teacher.five", "Teacher Five", "pass-123", "TEACHER");
        AuthenticatedActor outsiderActor = new AuthenticatedActor(
                AuthenticatedActor.ActorType.USER,
                "teacher-5",
                "teacher.five",
                "Teacher Five",
                null,
                java.util.List.of("TEACHER")
        );
        Assertions.assertThat(resourceAccessService.canAccessResource(outsiderActor, "QUESTION", "question-4"))
                .isFalse();
    }

    @Test
    void accessCheckWritesSuccessAuditLog() throws Exception {
        seedUser("teacher-6", "teacher.six", "Teacher Six", "pass-123", "TEACHER");
        seedClassMembership("membership-6", "class-6", "teacher-6", "TEACHER");
        seedOwnerScope("scope-6", "QUESTION", "question-6", null, "CLASS_ROOM", "class-6");

        String token = loginAndReturnToken("teacher.six", "pass-123");

        mockMvc.perform(get("/api/auth/access-check")
                        .header("Authorization", "Bearer " + token)
                        .param("resourceType", "QUESTION")
                        .param("resourceId", "question-6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true));

        Integer auditCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.audit_log where action_code = 'RESOURCE_ACCESS_CHECK' and result_code = 'SUCCESS' and resource_id = ?",
                Integer.class,
                "question-6"
        );
        Assertions.assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void accessCheckWritesForbiddenAuditLog() throws Exception {
        seedUser("teacher-7", "teacher.seven", "Teacher Seven", "pass-123", "TEACHER");
        seedUser("teacher-8", "teacher.eight", "Teacher Eight", "pass-123", "TEACHER");
        seedOwnerScope("scope-7", "QUESTION", "question-7", "teacher-7", "USER", "teacher-7");

        String token = loginAndReturnToken("teacher.eight", "pass-123");

        mockMvc.perform(get("/api/auth/access-check")
                        .header("Authorization", "Bearer " + token)
                        .param("resourceType", "QUESTION")
                        .param("resourceId", "question-7"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));

        Integer auditCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.audit_log where action_code = 'RESOURCE_ACCESS_CHECK' and result_code = 'FORBIDDEN' and resource_id = ?",
                Integer.class,
                "question-7"
        );
        Assertions.assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void flywayCreatesAuthBaselineTables() {
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from information_schema.tables
                        where table_schema = 'auth_service'
                          and table_name in ('user', 'role', 'user_role', 'class_room', 'class_membership',
                                             'teaching_assignment', 'resource_owner_scope', 'audit_log')
                        """,
                Integer.class
        );

        Assertions.assertThat(tableCount).isEqualTo(8);
    }

    @Test
    void authBaselineEnforcesUniqueConstraints() {
        jdbcTemplate.update(
                "insert into auth_service.`role` (id, code, name, status) values (?, ?, ?, ?)",
                "role-1", "UNIQUE_ROLE_CODE", "Unique Role", "ACTIVE"
        );

        Assertions.assertThatThrownBy(() -> jdbcTemplate.update(
                        "insert into auth_service.`role` (id, code, name, status) values (?, ?, ?, ?)",
                        "role-2", "UNIQUE_ROLE_CODE", "Duplicate Unique Role", "ACTIVE"
                ))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void authBaselineSupportsMembershipAndOwnershipRelationships() {
        jdbcTemplate.update(
                "insert into auth_service.`user` (id, username, display_name, status) values (?, ?, ?, ?)",
                "teacher-1", "teacher.one", "Teacher One", "ACTIVE"
        );
        jdbcTemplate.update(
                "insert into auth_service.class_room (id, code, name, grade_code, status) values (?, ?, ?, ?, ?)",
                "class-1", "G9-A", "Grade 9 A", "GRADE_9", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into auth_service.class_membership
                        (id, class_room_id, user_id, membership_role, status)
                        values (?, ?, ?, ?, ?)
                        """,
                "membership-1", "class-1", "teacher-1", "TEACHER", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into auth_service.resource_owner_scope
                        (id, resource_type, resource_id, owner_user_id, owner_scope_type, owner_scope_ref, status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                "scope-1", "QUESTION", "question-1", "teacher-1", "CLASS_ROOM", "class-1", "ACTIVE"
        );

        Integer membershipCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.class_membership where class_room_id = ? and user_id = ?",
                Integer.class,
                "class-1",
                "teacher-1"
        );
        Integer scopeCount = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.resource_owner_scope where resource_type = ? and resource_id = ?",
                Integer.class,
                "QUESTION",
                "question-1"
        );

        Assertions.assertThat(membershipCount).isEqualTo(1);
        Assertions.assertThat(scopeCount).isEqualTo(1);
    }

    private void seedUser(String userId, String username, String displayName, String rawPassword, String roleCode) {
        String roleId = jdbcTemplate.query(
                "select id from auth_service.`role` where code = ?",
                rs -> rs.next() ? rs.getString(1) : null,
                roleCode
        );
        if (roleId == null) {
            roleId = "role-" + roleCode;
            jdbcTemplate.update(
                    "insert into auth_service.`role` (id, code, name, status) values (?, ?, ?, ?)",
                    roleId,
                    roleCode,
                    roleCode,
                    "ACTIVE"
            );
        }

        jdbcTemplate.update(
                "insert into auth_service.`user` (id, username, display_name, status, password_hash) values (?, ?, ?, ?, ?)",
                userId,
                username,
                displayName,
                "ACTIVE",
                passwordEncoder.encode(rawPassword)
        );
        jdbcTemplate.update(
                "insert into auth_service.user_role (id, user_id, role_id) values (?, ?, ?)",
                "user-role-" + userId,
                userId,
                roleId
        );
    }

    private void seedClassMembership(String id, String classRoomId, String userId, String membershipRole) {
        ensureClassRoom(classRoomId);
        jdbcTemplate.update(
                """
                        insert into auth_service.class_membership
                        (id, class_room_id, user_id, membership_role, status)
                        values (?, ?, ?, ?, ?)
                        """,
                id,
                classRoomId,
                userId,
                membershipRole,
                "ACTIVE"
        );
    }

    private void seedTeachingAssignment(
            String id,
            String teacherUserId,
            String classRoomId,
            String subjectCode,
            String textbookVersionId
    ) {
        ensureClassRoom(classRoomId);
        jdbcTemplate.update(
                """
                        insert into auth_service.teaching_assignment
                        (id, teacher_user_id, class_room_id, subject_code, textbook_version_id, status)
                        values (?, ?, ?, ?, ?, ?)
                        """,
                id,
                teacherUserId,
                classRoomId,
                subjectCode,
                textbookVersionId,
                "ACTIVE"
        );
    }

    private void ensureClassRoom(String classRoomId) {
        Integer existing = jdbcTemplate.query(
                "select count(*) from auth_service.class_room where id = ?",
                rs -> rs.next() ? rs.getInt(1) : 0,
                classRoomId
        );
        if (existing != null && existing > 0) {
            return;
        }
        jdbcTemplate.update(
                "insert into auth_service.class_room (id, code, name, grade_code, status) values (?, ?, ?, ?, ?)",
                classRoomId,
                classRoomId,
                classRoomId,
                "GRADE_9",
                "ACTIVE"
        );
    }

    private void seedOwnerScope(
            String id,
            String resourceType,
            String resourceId,
            String ownerUserId,
            String ownerScopeType,
            String ownerScopeRef
    ) {
        jdbcTemplate.update(
                """
                        insert into auth_service.resource_owner_scope
                        (id, resource_type, resource_id, owner_user_id, owner_scope_type, owner_scope_ref, status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                id,
                resourceType,
                resourceId,
                ownerUserId,
                ownerScopeType,
                ownerScopeRef,
                "ACTIVE"
        );
    }

    private String extractJsonValue(String json, String key) {
        return json.split("\"" + key + "\":\"")[1].split("\"")[0];
    }

    private String loginAndReturnToken(String username, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        return extractJsonValue(loginResult.getResponse().getContentAsString(), "accessToken");
    }

    private void assertActionCount(String actionCode, String resultCode, String resourceId, int expected) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from auth_service.audit_log where action_code = ? and result_code = ? and resource_id = ?",
                Integer.class,
                actionCode,
                resultCode,
                resourceId
        );
        Assertions.assertThat(count).isEqualTo(expected);
    }
}
