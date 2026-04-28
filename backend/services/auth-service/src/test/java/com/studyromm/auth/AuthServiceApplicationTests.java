package com.studyromm.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studyromm.platform.testing.TraceIdAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                "role-1", "ADMIN", "Administrator", "ACTIVE"
        );

        Assertions.assertThatThrownBy(() -> jdbcTemplate.update(
                        "insert into auth_service.`role` (id, code, name, status) values (?, ?, ?, ?)",
                        "role-2", "ADMIN", "Duplicate Administrator", "ACTIVE"
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
}
