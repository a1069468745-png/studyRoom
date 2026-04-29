package com.studyromm.knowledge.api;

public record QuestionItem(
        String questionId,
        String questionType,
        String difficultyLevel,
        String sourceType,
        String reviewStatus,
        String stemMarkdown,
        String gradeCode,
        String subjectCode
) {
}
