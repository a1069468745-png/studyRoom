package com.studyromm.knowledge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studyromm.platform.testing.TraceIdAssertions;
import java.util.UUID;
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
    void dictionariesEndpointsReturnTextbookVersionsAndCurriculumNodes() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String textbookMathId = "tb-dev4a-1-" + suffix;
        String textbookEnglishId = "tb-dev4a-2-" + suffix;
        String rootNodeId = "node-dev4a-root-" + suffix;
        String childNode2Id = "node-dev4a-child-2-" + suffix;
        String childNode1Id = "node-dev4a-child-1-" + suffix;

        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                textbookMathId, "RJ-G9-MATH-DEV4A-" + suffix, "People Education Grade 9 Math", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                textbookEnglishId, "RJ-G9-ENG-DEV4A-" + suffix, "People Education Grade 9 English", "ENGLISH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, parent_node_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                rootNodeId, textbookMathId, null, "UNIT-01-" + suffix, "Unit 1", "UNIT", 1, "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, parent_node_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                childNode2Id, textbookMathId, rootNodeId, "CH-02-" + suffix, "Chapter 2", "CHAPTER", 2, "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, parent_node_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                childNode1Id, textbookMathId, rootNodeId, "CH-01-" + suffix, "Chapter 1", "CHAPTER", 1, "ACTIVE"
        );

        mockMvc.perform(get("/api/common/dictionaries/textbook-versions")
                        .param("subjectCode", "MATH")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$[?(@.textbookVersionId=='" + textbookMathId + "')]").isNotEmpty())
                .andExpect(jsonPath("$[0].subjectCode").value("MATH"));

        mockMvc.perform(get("/api/common/dictionaries/curriculum-nodes")
                        .param("textbookVersionId", textbookMathId)
                        .param("parentNodeId", rootNodeId)
                        .param("nodeType", "CHAPTER"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$[0].nodeId").value(childNode1Id))
                .andExpect(jsonPath("$[1].nodeId").value(childNode2Id));

        mockMvc.perform(get("/api/common/dictionaries"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdAssertions.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.curriculumNodeTypes[0]").value("STAGE"));
    }

    @Test
    void contentAssetEndpointsSupportDraftCreateAndPublishedQuery() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String textbookId = "tb-dev4b-1-" + suffix;
        String nodeId = "node-dev4b-1-" + suffix;
        String publishedAssetId = "asset-published-1-" + suffix;

        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                textbookId, "RJ-G9-MATH-DEV4B-" + suffix, "People Education Grade 9 Math", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                nodeId, textbookId, "KN-01-" + suffix, "Knowledge 1", "KNOWLEDGE", 1, "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.content_asset
                        (id, curriculum_node_id, asset_type, title, body_markdown, review_status, publish_status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                publishedAssetId, nodeId, "TEXT", "Published Asset", "body", "APPROVED", "PUBLISHED"
        );

        mockMvc.perform(post("/api/admin/knowledge/content-assets")
                        .contentType("application/json")
                        .content("""
                                {
                                  "curriculumNodeId":"%s",
                                  "assetType":"TEXT",
                                  "title":"Draft Asset",
                                  "bodyMarkdown":"draft body"
                                }
                                """.formatted(nodeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewStatus").value("DRAFT"))
                .andExpect(jsonPath("$.publishStatus").value("UNPUBLISHED"))
                .andExpect(jsonPath("$.assetId").isNotEmpty());

        mockMvc.perform(get("/api/admin/knowledge/content-assets")
                        .param("curriculumNodeId", nodeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists());

        mockMvc.perform(get("/api/client/knowledge/content-assets")
                        .param("curriculumNodeId", nodeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetId").value(publishedAssetId))
                .andExpect(jsonPath("$[0].publishStatus").value("PUBLISHED"));
    }

    @Test
    void questionEndpointsSupportQuestionCreateAndAdminQuery() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String textbookId = "tb-dev4c-1-" + suffix;
        String nodeId = "node-dev4c-1-" + suffix;
        String chapterNodeId = "node-dev4c-chapter-" + suffix;

        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                textbookId, "RJ-G9-MATH-DEV4C-" + suffix, "People Education Grade 9 Math", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                chapterNodeId, textbookId, "CH-DEV4C-" + suffix, "Chapter DEV4C", "CHAPTER", 1, "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, parent_node_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                nodeId, textbookId, chapterNodeId, "KN-DEV4C-" + suffix, "Knowledge DEV4C", "KNOWLEDGE", 1, "ACTIVE"
        );

        mockMvc.perform(post("/api/admin/questions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "questionType":"CHOICE",
                                  "difficultyLevel":"MEDIUM",
                                  "sourceType":"KNOWLEDGE_BASE",
                                  "stemMarkdown":"What is 2 + 2?",
                                  "gradeCode":"GRADE_9",
                                  "subjectCode":"MATH",
                                  "answerType":"TEXT",
                                  "answerContent":"4",
                                  "analysisMarkdown":"2+2 equals 4",
                                  "stepMarkdown":"step-1",
                                  "knowledgeNodeIds":["%s"],
                                  "curriculumBindings":[{"nodeId":"%s","nodeType":"CHAPTER"}]
                                }
                                """.formatted(nodeId, chapterNodeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").isNotEmpty())
                .andExpect(jsonPath("$.reviewStatus").value("DRAFT"))
                .andExpect(jsonPath("$.answerVersion").value(1))
                .andExpect(jsonPath("$.analysisVersion").value(1));

        mockMvc.perform(get("/api/admin/questions")
                        .param("subjectCode", "MATH")
                        .param("reviewStatus", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionId").isNotEmpty())
                .andExpect(jsonPath("$[0].subjectCode").value("MATH"));
    }

    @Test
    void createQuestionFailsWhenKnowledgeBindingMissing() throws Exception {
        mockMvc.perform(post("/api/admin/questions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "questionType":"CHOICE",
                                  "difficultyLevel":"MEDIUM",
                                  "sourceType":"KNOWLEDGE_BASE",
                                  "stemMarkdown":"What is 3 + 3?",
                                  "gradeCode":"GRADE_9",
                                  "subjectCode":"MATH",
                                  "answerType":"TEXT",
                                  "answerContent":"6",
                                  "analysisMarkdown":"3+3 equals 6",
                                  "knowledgeNodeIds":[]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void reviewQuestionApprovesDraftQuestion() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String questionId = "question-review-" + suffix;
        jdbcTemplate.update(
                """
                        insert into question_service.question
                        (id, question_type, difficulty_level, source_type, review_status, stem_markdown, grade_code, subject_code)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                questionId, "CHOICE", "MEDIUM", "KNOWLEDGE_BASE", "DRAFT", "review me", "GRADE_9", "MATH"
        );

        mockMvc.perform(post("/api/admin/questions/{questionId}/review", questionId)
                        .contentType("application/json")
                        .content("""
                                {
                                  "reviewStatus":"APPROVED",
                                  "reviewComment":"ok"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(questionId))
                .andExpect(jsonPath("$.reviewStatus").value("APPROVED"));
    }

    @Test
    void reviewQuestionFailsWhenCurrentStatusIsNotDraft() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String questionId = "question-reviewed-" + suffix;
        jdbcTemplate.update(
                """
                        insert into question_service.question
                        (id, question_type, difficulty_level, source_type, review_status, stem_markdown, grade_code, subject_code)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                questionId, "CHOICE", "MEDIUM", "KNOWLEDGE_BASE", "APPROVED", "already reviewed", "GRADE_9", "MATH"
        );

        mockMvc.perform(post("/api/admin/questions/{questionId}/review", questionId)
                        .contentType("application/json")
                        .content("""
                                {
                                  "reviewStatus":"REJECTED",
                                  "reviewComment":"late reject"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    void reviewQuestionFailsWhenQuestionNotFound() throws Exception {
        mockMvc.perform(post("/api/admin/questions/{questionId}/review", "question-missing")
                        .contentType("application/json")
                        .content("""
                                {
                                  "reviewStatus":"APPROVED",
                                  "reviewComment":"missing"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
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
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String textbookId = "tb-1-" + suffix;
        String nodeId = "node-1-" + suffix;
        String questionId = "question-1-" + suffix;
        String optionId = "option-1-" + suffix;
        String jobId1 = "job-1-" + suffix;
        String jobId2 = "job-2-" + suffix;
        String idempotencyKey = "teacher-1:paper-1:" + suffix;

        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                textbookId, "RJ-G9-MATH-" + suffix, "People Education Grade 9 Math", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into knowledge_service.curriculum_node
                        (id, textbook_version_id, node_code, node_name, node_type, sort_order, status)
                        values (?, ?, ?, ?, ?, ?, ?)
                        """,
                nodeId, textbookId, "UNIT-01-" + suffix, "Unit 1", "UNIT", 1, "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into question_service.question
                        (id, question_type, difficulty_level, source_type, review_status, stem_markdown, grade_code, subject_code)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                questionId, "CHOICE", "MEDIUM", "KNOWLEDGE_BASE", "DRAFT", "What is 1 + 1?", "GRADE_9", "MATH"
        );
        jdbcTemplate.update(
                """
                        insert into question_service.question_option
                        (id, question_id, option_code, option_content, is_correct, sort_order)
                        values (?, ?, ?, ?, ?, ?)
                        """,
                optionId, questionId, "A", "2", true, 1
        );
        jdbcTemplate.update(
                """
                        insert into job_service.job_task
                        (id, task_type, status, idempotency_key, trace_id)
                        values (?, ?, ?, ?, ?)
                        """,
                jobId1, "PAPER_GENERATION", "PENDING", idempotencyKey, "trace-job-1"
        );

        Assertions.assertThatThrownBy(() -> jdbcTemplate.update(
                        """
                                insert into job_service.job_task
                                (id, task_type, status, idempotency_key, trace_id)
                                values (?, ?, ?, ?, ?)
                                """,
                        jobId2, "PAPER_GENERATION", "PENDING", idempotencyKey, "trace-job-2"
                ))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void firstWaveBaselineSupportsExamPlanningRelationships() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String textbookId = "tb-2-" + suffix;
        String planId = "plan-1-" + suffix;
        String targetId = "target-1-" + suffix;
        String sessionId = "session-1-" + suffix;

        jdbcTemplate.update(
                """
                        insert into knowledge_service.textbook_version
                        (id, code, name, subject_code, status)
                        values (?, ?, ?, ?, ?)
                        """,
                textbookId, "RJ-G9-MATH-EXAM-" + suffix, "People Education Grade 9 Math Exam", "MATH", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_plan
                        (id, title, scope_mode, textbook_version_id, status)
                        values (?, ?, ?, ?, ?)
                        """,
                planId, "Grade 9 Weekly Exam", "UNIT_SET", textbookId, "DRAFT"
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_plan_target
                        (id, exam_plan_id, target_type, target_ref, status)
                        values (?, ?, ?, ?, ?)
                        """,
                targetId, planId, "CLASS_ROOM", "class-1", "ACTIVE"
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_session
                        (id, exam_plan_id, status, duration_minutes)
                        values (?, ?, ?, ?)
                        """,
                sessionId, planId, "SCHEDULED", 90
        );
        jdbcTemplate.update(
                """
                        insert into exam_service.exam_submission
                        (id, exam_session_id, student_id, submit_status, client_id)
                        values (?, ?, ?, ?, ?)
                        """,
                "submission-1-" + suffix, sessionId, "student-1", "IN_PROGRESS", "web-client"
        );

        Integer targetCount = jdbcTemplate.queryForObject(
                "select count(*) from exam_service.exam_plan_target where exam_plan_id = ?",
                Integer.class,
                planId
        );
        Integer submissionCount = jdbcTemplate.queryForObject(
                "select count(*) from exam_service.exam_submission where exam_session_id = ?",
                Integer.class,
                sessionId
        );

        Assertions.assertThat(targetCount).isEqualTo(1);
        Assertions.assertThat(submissionCount).isEqualTo(1);
    }
}
