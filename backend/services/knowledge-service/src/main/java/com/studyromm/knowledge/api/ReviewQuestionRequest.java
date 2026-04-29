package com.studyromm.knowledge.api;

public record ReviewQuestionRequest(
        String reviewStatus,
        String reviewComment
) {
}
