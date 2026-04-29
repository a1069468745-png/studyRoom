package com.studyromm.knowledge.api;

public record CreateContentAssetRequest(
        String curriculumNodeId,
        String assetType,
        String title,
        String bodyMarkdown
) {
}
