package com.studyromm.knowledge.api;

public record ContentAssetItem(
        String assetId,
        String curriculumNodeId,
        String assetType,
        String title,
        String bodyMarkdown,
        String reviewStatus,
        String publishStatus
) {
}
