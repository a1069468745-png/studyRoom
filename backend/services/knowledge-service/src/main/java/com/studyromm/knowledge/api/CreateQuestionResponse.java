package com.studyromm.knowledge.api;

public record CreateQuestionResponse(
        String questionId,
        String reviewStatus,
        Integer answerVersion,
        Integer analysisVersion
) {
}
