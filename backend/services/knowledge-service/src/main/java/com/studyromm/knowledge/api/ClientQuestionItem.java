package com.studyromm.knowledge.api;

public record ClientQuestionItem(
        String questionId,
        String questionType,
        String difficultyLevel,
        String stemMarkdown,
        String gradeCode,
        String subjectCode
) {
}
