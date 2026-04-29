package com.studyromm.knowledge.api;

public record CreateContentAssetResponse(
        String assetId,
        String reviewStatus,
        String publishStatus
) {
}
