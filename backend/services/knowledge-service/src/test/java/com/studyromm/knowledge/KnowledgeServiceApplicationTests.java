package com.studyromm.knowledge;

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
class KnowledgeServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void healthEndpointReturnsServiceNameAndTraceId() throws Exception {
        mockMvc.perform(get("/api/common/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.service").value("knowledge-service"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void commonTaskEndpointReturnsBootstrapContract() throws Exception {
        mockMvc.perform(get("/api/common/tasks/job_knowledge_bootstrap"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.taskId").value("job_knowledge_bootstrap"))
                .andExpect(jsonPath("$.taskType").value("BOOTSTRAP"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.progress").value(0))
                .andExpect(jsonPath("$.retryable").value(true))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void unknownRouteReturnsUnifiedErrorStructure() throws Exception {
        mockMvc.perform(get("/api/knowledge/missing"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void flywayCreatesFirstWaveSchemasAndTables() {
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from information_schema.tables
                        where (table_schema = 'knowledge_service' and table_name in ('textbook_version', 'curriculum_node', 'content_asset'))
                           or (table_schema = 'question_service' and table_name in ('question', 'question_option', 'question_answer', 'question_analysis',
                                                                                      'question_knowledge', 'question_curriculum_node'))
                           or (table_schema = 'exam_service' and table_name in ('exam_plan', 'exam_plan_target', 'exam_session', 'exam_submission'))
                           or (table_schema = 'job_service' and table_name in ('job_task'))
                        """,
                Integer.class
        );

        Assertions.assertThat(tableCount).isEqualTo(14);
    }

    @Test
    void firstWaveBaselineEnforcesQuestionAndTaskConstraints() {
        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                "tb-1", "RJ-G9-MATH", "People Education Grade 9 Math", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                "node-1", "tb-1", "UNIT-01", "Unit 1", "UNIT", 1, "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into question_service.question
                        (id, question_type, difficulty_level, source_type, review_status, stem_markdown, grade_code, subject_code)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                "question-1", "CHOICE", "MEDIUM", "KNOWLEDGE_BASE", "DRAFT", "What is 1 + 1?", "GRADE_9", "MATH"
        );
        jdbcTemplate.update(
                """
                        insert into question_service.question_option
                        (id, question_id, option_code, option_content, is_correct, sort_order)
                        values (?, ?, ?, ?, ?, ?)
                        """,
                "option-1", "question-1", "A", "2", true, 1
        );
        jdbcTemplate.update(
                """
                        insert into job_service.job_task
                        (id, task_type, status, idempotency_key, trace_id)
                        values (?, ?, ?, ?, ?)
                        """,
                "job-1", "PAPER_GENERATION", "PENDING", "teacher-1:paper-1", "trace-job-1"
        );

        Assertions.assertThatThrownBy(() -> jdbcTemplate.update(
                        """
                                insert into job_service.job_task
                                (id, task_type, status, idempotency_key, trace_id)
                                values (?, ?, ?, ?, ?)
                                """,
                        "job-2", "PAPER_GENERATION", "PENDING", "teacher-1:paper-1", "trace-job-2"
                ))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void firstWaveBaselineSupportsExamPlanningRelationships() {
        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                "tb-2", "RJ-G9-MATH-EXAM", "People Education Grade 9 Math Exam", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_plan
                        (id, title, scope_mode, textbook_version_id, status)
                        values (?, ?, ?, ?, ?)
                        """,
                "plan-1", "Grade 9 Weekly Exam", "UNIT_SET", "tb-2", "DRAFT"
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_plan_target
                        (id, exam_plan_id, target_type, target_ref, status)
                        values (?, ?, ?, ?, ?)
                        """,
                "target-1", "plan-1", "CLASS_ROOM", "class-1", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_session
                        (id, exam_plan_id, status, duration_minutes)
                        values (?, ?, ?, ?)
                        """,
                "session-1", "plan-1", "SCHEDULED", 90
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_submission
                        (id, exam_session_id, student_id, submit_status, client_id)
                        values (?, ?, ?, ?, ?)
                        """,
                "submission-1", "session-1", "student-1", "IN_PROGRESS", "web-client"
        );

        Integer targetCount = jdbcTemplate.queryForObject(
                "select count(*) from exam_service.exam_plan_target where exam_plan_id = ?",
                Integer.class,
                "plan-1"
        );
        Integer submissionCount = jdbcTemplate.queryForObject(
                "select count(*) from exam_service.exam_submission where exam_session_id = ?",
                Integer.class,
                "session-1"
        );

        Assertions.assertThat(targetCount).isEqualTo(1);
        Assertions.assertThat(submissionCount).isEqualTo(1);
    }
}
