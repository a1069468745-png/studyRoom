package com.studyromm.platform.service.task;

public record TaskStatusResponse(
        String taskId,
        String taskType,
        TaskStatus status,
        int progress,
        String resultSummary,
        String errorCode,
        String errorMessage,
        boolean retryable,
        String traceId
) {
}
