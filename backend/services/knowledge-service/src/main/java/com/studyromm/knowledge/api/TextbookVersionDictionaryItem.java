package com.studyromm.knowledge.api;

public record TextbookVersionDictionaryItem(
        String textbookVersionId,
        String code,
        String name,
        String subjectCode,
        String status
) {
}
