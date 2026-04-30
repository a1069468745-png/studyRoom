package com.studyromm.knowledge.application;

import com.studyromm.knowledge.api.CreateQuestionRequest;
import com.studyromm.knowledge.api.CreateQuestionResponse;
import com.studyromm.knowledge.api.ClientQuestionItem;
import com.studyromm.knowledge.api.QuestionItem;
import com.studyromm.knowledge.api.ReviewQuestionRequest;
import com.studyromm.knowledge.api.ReviewQuestionResponse;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBankService {
    private static final Set<String> REVIEW_TARGET_STATUSES = Set.of("APPROVED", "REJECTED");

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public QuestionBankService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<QuestionItem> listAdminQuestions(String subjectCode, String reviewStatus) {
        StringBuilder sql = new StringBuilder(
                """
                        select id, question_type, difficulty_level, source_type, review_status, stem_markdown, grade_code, subject_code
                        from question_service.question
                        where is_deleted = false
                        """
        );
        Map<String, Object> params = new java.util.HashMap<>();
        if (!isBlank(subjectCode)) {
            sql.append(" and subject_code = :subjectCode");
            params.put("subjectCode", subjectCode);
        }
        if (!isBlank(reviewStatus)) {
            sql.append(" and review_status = :reviewStatus");
            params.put("reviewStatus", reviewStatus);
        }
        sql.append(" order by created_at desc, id desc");
        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> new QuestionItem(
                rs.getString("id"),
                rs.getString("question_type"),
                rs.getString("difficulty_level"),
                rs.getString("source_type"),
                rs.getString("review_status"),
                rs.getString("stem_markdown"),
                rs.getString("grade_code"),
                rs.getString("subject_code")
        ));
    }

    public List<ClientQuestionItem> listClientQuestions(String subjectCode, String gradeCode, String questionType) {
        StringBuilder sql = new StringBuilder(
                """
                        select id, question_type, difficulty_level, stem_markdown, grade_code, subject_code
                        from question_service.question
                        where is_deleted = false
                          and review_status = 'APPROVED'
                        """
        );
        Map<String, Object> params = new java.util.HashMap<>();
        if (!isBlank(subjectCode)) {
            sql.append(" and subject_code = :subjectCode");
            params.put("subjectCode", subjectCode);
        }
        if (!isBlank(gradeCode)) {
            sql.append(" and grade_code = :gradeCode");
            params.put("gradeCode", gradeCode);
        }
        if (!isBlank(questionType)) {
            sql.append(" and question_type = :questionType");
            params.put("questionType", questionType);
        }
        sql.append(" order by created_at desc, id desc");
        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> new ClientQuestionItem(
                rs.getString("id"),
                rs.getString("question_type"),
                rs.getString("difficulty_level"),
                rs.getString("stem_markdown"),
                rs.getString("grade_code"),
                rs.getString("subject_code")
        ));
    }

    @Transactional
    public CreateQuestionResponse createQuestion(CreateQuestionRequest request) {
        validateCreateRequest(request);

        String questionId = "question-" + UUID.randomUUID();
        jdbcTemplate.update(
                """
                        insert into question_service.question
                        (id, question_type, difficulty_level, source_type, review_status, stem_markdown, grade_code, subject_code)
                        values (:id, :questionType, :difficultyLevel, :sourceType, :reviewStatus, :stemMarkdown, :gradeCode, :subjectCode)
                        """,
                Map.of(
                        "id", questionId,
                        "questionType", request.questionType(),
                        "difficultyLevel", request.difficultyLevel(),
                        "sourceType", request.sourceType(),
                        "reviewStatus", "DRAFT",
                        "stemMarkdown", request.stemMarkdown(),
                        "gradeCode", request.gradeCode(),
                        "subjectCode", request.subjectCode()
                )
        );

        String answerId = "answer-" + UUID.randomUUID();
        jdbcTemplate.update(
                """
                        insert into question_service.question_answer
                        (id, question_id, answer_type, answer_content, answer_version)
                        values (:id, :questionId, :answerType, :answerContent, :answerVersion)
                        """,
                Map.of(
                        "id", answerId,
                        "questionId", questionId,
                        "answerType", request.answerType(),
                        "answerContent", request.answerContent(),
                        "answerVersion", 1
                )
        );

        String analysisId = "analysis-" + UUID.randomUUID();
        jdbcTemplate.update(
                """
                        insert into question_service.question_analysis
                        (id, question_id, analysis_version, analysis_markdown, step_markdown)
                        values (:id, :questionId, :analysisVersion, :analysisMarkdown, :stepMarkdown)
                        """,
                Map.of(
                        "id", analysisId,
                        "questionId", questionId,
                        "analysisVersion", 1,
                        "analysisMarkdown", request.analysisMarkdown(),
                        "stepMarkdown", request.stepMarkdown()
                )
        );

        for (String nodeId : request.knowledgeNodeIds()) {
            jdbcTemplate.update(
                    """
                            insert into question_service.question_knowledge
                            (id, question_id, curriculum_node_id, relation_type)
                            values (:id, :questionId, :nodeId, :relationType)
                            """,
                    Map.of(
                            "id", "qk-" + UUID.randomUUID(),
                            "questionId", questionId,
                            "nodeId", nodeId,
                            "relationType", "PRIMARY"
                    )
            );
        }

        if (request.curriculumBindings() != null) {
            for (CreateQuestionRequest.CurriculumBinding binding : request.curriculumBindings()) {
                if (binding == null || isBlank(binding.nodeId()) || isBlank(binding.nodeType())) {
                    continue;
                }
                jdbcTemplate.update(
                        """
                                insert into question_service.question_curriculum_node
                                (id, question_id, curriculum_node_id, node_type)
                                values (:id, :questionId, :nodeId, :nodeType)
                                """,
                        Map.of(
                                "id", "qcn-" + UUID.randomUUID(),
                                "questionId", questionId,
                                "nodeId", binding.nodeId(),
                                "nodeType", binding.nodeType()
                        )
                );
            }
        }

        return new CreateQuestionResponse(questionId, "DRAFT", 1, 1);
    }

    @Transactional
    public ReviewQuestionResponse reviewQuestion(String questionId, ReviewQuestionRequest request) {
        if (isBlank(questionId) || request == null || isBlank(request.reviewStatus())) {
            throw new IllegalArgumentException("questionId and reviewStatus are required");
        }
        String targetStatus = request.reviewStatus().trim().toUpperCase();
        if (!REVIEW_TARGET_STATUSES.contains(targetStatus)) {
            throw new IllegalArgumentException("reviewStatus must be APPROVED or REJECTED");
        }

        String currentStatus = jdbcTemplate.query(
                        """
                                select review_status
                                from question_service.question
                                where id = :questionId and is_deleted = false
                                """,
                        Map.of("questionId", questionId),
                        rs -> rs.next() ? rs.getString("review_status") : null
                );
        if (currentStatus == null) {
            throw new NoSuchElementException("question not found");
        }
        if (!"DRAFT".equalsIgnoreCase(currentStatus)) {
            throw new IllegalStateException("only DRAFT question can be reviewed");
        }

        jdbcTemplate.update(
                """
                        update question_service.question
                        set review_status = :reviewStatus, updated_at = current_timestamp
                        where id = :questionId
                        """,
                Map.of(
                        "reviewStatus", targetStatus,
                        "questionId", questionId
                )
        );
        return new ReviewQuestionResponse(questionId, targetStatus);
    }

    private void validateCreateRequest(CreateQuestionRequest request) {
        if (request == null
                || isBlank(request.questionType())
                || isBlank(request.difficultyLevel())
                || isBlank(request.sourceType())
                || isBlank(request.stemMarkdown())
                || isBlank(request.gradeCode())
                || isBlank(request.subjectCode())
                || isBlank(request.answerType())
                || isBlank(request.answerContent())
                || isBlank(request.analysisMarkdown())) {
            throw new IllegalArgumentException("required fields are missing");
        }
        if (request.knowledgeNodeIds() == null || request.knowledgeNodeIds().isEmpty()) {
            throw new IllegalArgumentException("question must bind at least one knowledge node");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
