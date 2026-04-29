package com.studyromm.knowledge.api;

public record CurriculumNodeDictionaryItem(
        String nodeId,
        String textbookVersionId,
        String parentNodeId,
        String nodeCode,
        String nodeName,
        String nodeType,
        Integer sortOrder,
        String status
) {
}
