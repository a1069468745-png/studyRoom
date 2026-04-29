package com.studyromm.knowledge.api;

import java.util.List;

public record CreateQuestionRequest(
        String questionType,
        String difficultyLevel,
        String sourceType,
        String stemMarkdown,
        String gradeCode,
        String subjectCode,
        String answerType,
        String answerContent,
        String analysisMarkdown,
        String stepMarkdown,
        List<String> knowledgeNodeIds,
        List<CurriculumBinding> curriculumBindings
) {
    public record CurriculumBinding(String nodeId, String nodeType) {
    }
}
